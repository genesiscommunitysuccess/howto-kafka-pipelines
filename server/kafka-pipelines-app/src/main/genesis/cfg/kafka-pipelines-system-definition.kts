/**
 * System              : Genesis Business Library
 * Sub-System          : multi-pro-code-test Configuration
 * Version             : 1.0
 * Copyright           : (c) Genesis
 * Date                : 2022-03-18
 * Function : Provide system definition config for multi-pro-code-test.
 *
 * Modification History
 */
systemDefinition {
    global {
        // the following have been set up as global definitions to be used as part of the pipelines script, these can be host specific as well
        item("BOOTSTRAP_SERVER", "localhost:9092")
        item("CONSUMER_GROUP_ID", "price-consumer")
        item("PRODUCER_CLIENT_ID", "price-producer")
        // see topic creation comment in docker-compose.yml
        item("KAFKA_TOPIC", "price-topic")
        // for running kafka locally we have set all security configurations to be PLAINTEXT, ensure that you use the appropriate security config for your application
        item("KAFKA_SECURITY_CONFIG", "PLAINTEXT")
    }

    systems {

    }

}