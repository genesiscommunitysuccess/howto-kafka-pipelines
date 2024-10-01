package scripts

import global.genesis.pipeline.api.db.DbOperation
import kotlinx.coroutines.flow.flow
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.serialization.DoubleDeserializer
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
// here for example our key is of type String and value will be of type Double, so it becomes kafkaSource<String, Int>
val source = kafkaSource<String, Double> {
    // now you can define the kafka specific configurations, these can either be hardcoded into this script or configurable via system definitions as shown below
    bootstrapServers = systemDefinition.getItem("BOOTSTRAP_SERVER").toString()
    groupId = systemDefinition.getItem("CONSUMER_GROUP_ID").toString()
    // here we are using the out of box deserialisers for simplicity but as mentioned in the README you can create your own as per your requirement
    // you can provide any deserialiser as per your requirement as long as it is of type Deserializer<T> where T is the type of your key / value respectively specified when initialising kafkaSource above
    keyDeserializer = StringDeserializer()
    // here you can use PriceReceivedDeserialiser() for the value and the ConsumerRecord returned would have key String, value PriceReceived
    valueDeserializer = DoubleDeserializer()
    // ensure that this kafka topic has been created, in this example we do so when initialising the docker container
    topic = systemDefinition.getItem("KAFKA_TOPIC").toString()
    securityProtocol = systemDefinition.getItem("KAFKA_SECURITY_CONFIG").toString()
    // if you would like to provide any additional kafka consumer config you can do this like so
    // additionalConfig = mapOf("auto.offset.reset" to "earliest")
}

// initialise the operator that converts the output of the kafka source (ConsumerRecords<String, Int> and returns a flow of each ConsumerRecord object in that batch
val splitOperator: SplitOperator<ConsumerRecords<String, Double>, ConsumerRecord<String, Double>> = SplitOperator { consumerRecords ->
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
                // as we are simply inserting into the database, we construct a PriceReceived object using the String key which represents the Isin and Double value which represents the bond price
                PriceReceived(it.key(), it.value())
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
