package scripts

import global.genesis.gen.dao.enums.kafka_pipelines.price_received.Side
import global.genesis.pipeline.api.db.DbOperation
import kotlinx.coroutines.flow.flow
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import global.genesis.kafka.JSONDeserialiser
import org.apache.kafka.common.serialization.StringDeserializer

/**
 *
 *   System              : kafka-pipelines
 *   Sub-System          : kafka-pipelines Configuration
 *   Version             : 1.0
 *   Copyright           : (c) GENESIS
 *   Date                : 2021-09-07
 *
 *   Function : Provide Data Pipeline Configuration for kafka-pipelines.
 *
 *   Modification History
 *
 */

// initialise your kafka source by specifying the type of the Key and Value for the record in the kafka topic
// here for example our key is of type String and value will be of type Map<String, Any>, so it becomes kafkaSource<String, Map<String, Any>>
val source = kafkaSource<String, Map<String, Any>> {
    // now you can define the kafka specific configurations, these can either be hardcoded into this script or configurable via system definitions as shown below
    bootstrapServers = systemDefinition.getItem("BOOTSTRAP_SERVER").toString()
    groupId = systemDefinition.getItem("CONSUMER_GROUP_ID").toString()
    // here we are using the out of box deserialisers for simplicity but as mentioned in the README you can create your own as per your requirement
    // you can provide any deserialiser as per your requirement as long as it is of type Deserializer<T> where T is the type of your key / value respectively specified when initialising kafkaSource above
    keyDeserializer = StringDeserializer()
    // here we are using the custom deserialiser we created to convert the value in the kafka record to Map<String, Any>
    valueDeserializer = JSONDeserialiser()
    // ensure that this kafka topic has been created, in this example we do so when initialising the docker container
    topic = systemDefinition.getItem("KAFKA_TOPIC").toString()
    securityProtocol = systemDefinition.getItem("KAFKA_SECURITY_CONFIG").toString()
    // if you would like to provide any additional kafka consumer config you can do this like so
    // additionalConfig = mapOf("auto.offset.reset" to "earliest")
}

// initialise the operator that converts the output of the kafka source (ConsumerRecords<String, Map<String, Any>> and returns a flow of each ConsumerRecord object in that batch
val splitOperator: SplitOperator<ConsumerRecords<String, Map<String, Any>>, ConsumerRecord<String, Map<String, Any>>> = SplitOperator { consumerRecords ->
    flow {
        // here we iterate over every ConsumerRecord
        consumerRecords.forEach {
            // here we emit it to the resulting flow
            emit(it)
        }
    }
}

pipelines {
    pipeline("KAFKA_TO_DB_PIPELINE") {
        // sourcing from the kafka source as defined above
        source(source)
            // split operator to split up batch of ConsumerRecords as defined above
            .split(splitOperator)
            .map {
                // this is where you can perform any logic you want now that you have access to the key and value of each record read from the kafka topic
                // here we construct a PriceReceived object using the value map, since the map has type Any for its values we can directly convert to the type we expect it to be
                val map = it.value()
                PriceReceived(
                    isin = map["ISIN"] as String,
                    // the DateTime field will be stored as Long, therefore we process it as a Long value and convert it to the Joda DateTime that the Genesis DAO expects (same for priceDate below)
                    priceDatetime = DateTime(map["PRICE_DATETIME"] as Long),
                    bondPrice = map["BOND_PRICE"] as Double,
                    maxQuantity = map["MAX_QUANTITY"] as Int,
                    indicative = map["INDICATIVE"] as Boolean,
                    priceDate = DateTime(map["PRICE_DATE"] as Long),
                    // because Side is created as a separate enum in both the PriceReceived and PricePublished tables, we have stored the value in this map as a String
                    // here we convert the String value to the correct enum type using valueOf
                    side = Side.valueOf(map["SIDE"] as String)
                )
            }
            // in the case where the element cannot be converted to the type we are expecting, we deal with the error here
            // the action we have chosen is to SKIP_ELEMENT which simply skips this element and continues with the rest of the batch of kafka records
            // other options are: SKIP_STREAM, STOP_PIPELINE - see (https://docs.genesis.global/docs/develop/server-capabilities/integrations/data-pipelines/#onoperationerror) for more info on this error handler
            .onOperationError { element, _, throwable ->
                LOG.error("Element: {} could not be converted to PriceReceived object", element, throwable)
                OperationErrorAction.SKIP_ELEMENT
            }
            // here we are converting each PriceReceived object into a DbOperation to insert into the database
            .map {
                // in order to use the database sink we must provide it a DbOperation to perform - in this case it's an insert on each PriceReceived object provided by the above operation
                DbOperation.Insert(it)
            }
            // here we are using a simple database sink to perform the above operation - txDbSink is a transactional database sink
            // if you would like to use a non-transactional database sink, simple replace txDbSink() with dbSink()
            .sink(txDbSink())
    }
}
