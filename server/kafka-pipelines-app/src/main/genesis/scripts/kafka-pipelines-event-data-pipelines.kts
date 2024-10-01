import global.genesis.kafka.ProgrammaticPriceSource
import org.apache.kafka.clients.producer.ProducerRecord
import global.genesis.kafka.JSONSerialiser
import org.apache.kafka.common.serialization.StringSerializer

// this pipelines file has been created to contain all pipelines that need to be linked to event handlers
// this is because these pipelines need to be run on the same process that is calling the events, see kafka-pipelines-processes.xml for more info on the process set up
// other pipelines which have no link to the event handler should be created in kafka-pipelines-data-pipelines.kts

// initialise your kafka sink by specifying the type of the Key and Value for the record in the kafka topic
// here for example our key is of type String and value will be of type Map<String, Any>, so it becomes kafkaSink<String, Map<String, Any>>
val sink = kafkaSink<String, Map<String, Any>> {
    // now you can define the kafka specific configurations, these can either be hardcoded into this script or configurable via system definitions as shown below
    bootstrapServers = systemDefinition.getItem("BOOTSTRAP_SERVER").toString()
    clientId = systemDefinition.getItem("PRODUCER_CLIENT_ID").toString()
    // here we are using the out of box serialisers for simplicity but as mentioned in the README you can create your own as per your requirement
    // you can provide any serialiser as per your requirement as long as it is of type Serializer<T> where T is the type of your key / value respectively specified when initialising kafkaSink above
    keySerializer = StringSerializer()
    // here we are using the custom serialiser we created to convert the Map<String, Any> value we provide to what kafka will process
    valueSerializer = JSONSerialiser()
    securityProtocol = systemDefinition.getItem("KAFKA_SECURITY_CONFIG").toString()
    // if you would like to provide any additional kafka producer config you can do this like so
    // additionalConfig = mapOf("request.timeout.ms" to "500")
}

// storing the kafka topic name to be used when publishing to kafka
// ensure that this kafka topic has been created, in this example we do so when initialising the docker container
val kafkaTopic = systemDefinition.getItem("KAFKA_TOPIC").toString()

pipelines {
    pipeline("EVENT_TO_KAFKA_PIPELINE") {
        // here we are using the ProgrammaticPriceSource which allows the event to send data through to the source
        source(ProgrammaticPriceSource)
            // here we are converting each PriceReceived object to a ProducerRecord for kafka to publish
            // you can add additional logic here to dynamically send to a different kafka topic depending on your requirement
            // for simplicity we are sending to the same topic each time as defined above by the system definitions
            .map { ProducerRecord<String, Map<String, Any>>(kafkaTopic,
                // we are using the concatenation of the fields defined in the primary key of the PriceReceived table as our String key
                "${it.isin}|${it.priceDatetime}",
                // here we are constructing our map using the values in our PriceReceived record
                // these keys will be used to obtain the record values when we read this map in kafka-pipelines-data-pipelines.kts
                mapOf(
                "ISIN" to it.isin,
                "PRICE_DATETIME" to it.priceDatetime,
                "BOND_PRICE" to it.bondPrice,
                "MAX_QUANTITY" to it.maxQuantity,
                "INDICATIVE" to it.indicative,
                "PRICE_DATE" to it.priceDate,
                 // because Side is created as a separate enum in both the PriceReceived and PricePublished tables, we take the value of the enum and store it as a string
                 // this will then be converted to the correct enum type in kafka-pipelines-data-pipelines.kts when we read its value
                "SIDE" to it.side.toString()
            )) }
            // pushing to the kafka sink as defined above
            .sink(sink)
    }
}