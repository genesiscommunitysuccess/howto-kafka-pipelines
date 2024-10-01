# kafka-pipelines

## Introduction

This application has been designed to demonstrate how Kafka can be used to either poll OR publish to a kafka topic.
Two pipelines have been constructed to demonstrate these use cases.
The first pipeline is sourced by an event handler and uses the data from the event to publish to a kafka topic.
The second pipeline then reads from this kafka topic and inserts it into the database.
We have split these pipelines into 2 different scripts so that they can be run by 2 separate processes.
The example uses two simple tables called `PRICE_PUBLISHED` and `PRICE_RECEIVED` which both contain the same fields:
- `PRICE_PUBLISHED` is used to store records that the user publishes to kafka
- `PRICE_RECEIVED` is to store records that are consumed from kafka
We will be publishing a generic JSON object in the form Map<String, Any> to the kafka topic and reading the values we need to construct the record that will be inserted into the database.

The Genesis framework allows you to embed a custom implementation of a serialiser/deserialiser into your project, therefore we have created a basic serialiser/deserialiser to transform the value 
in the kafka record to/from Map<String, Any>. For more information on how to create your own serialiser/deserialiser class please see the following: [Custom Serializers in Apache Kafka](https://www.baeldung.com/kafka-custom-serializer)

## Adding Kafka to your Genesis application
When needing to integrate with kafka in your Genesis application, there are a few set up steps which must be completed. Overviews for each step have been provided below and further
detail can be found in the relevant linked files:

1. Set up docker image of kafka:
   You have the option to host your own instance of kafka and replace the respective system definitions with the ones for your instance (see step 4)
   but for this example we show how to use docker to run an image of kafka, see [docker-compose.yml](docker-compose.yml).

2. Add dependencies:
   There are 3 dependencies which we need to add to the `build.gradle.kts` file of our app. See [build.gradle.kts](server/kafka-pipelines-app/build.gradle.kts).

3. Set up serialiser/deserialiser:
   We have created a basic JSON serialiser/deserialiser. See [JSONSerialiser](server/kafka-pipelines-app/src/main/kotlin/global/genesis/kafka/JSONSerialiser.kt) and [JSONDeserialiser](server/kafka-pipelines-app/src/main/kotlin/global/genesis/kafka/JSONDeserialiser.kt).

4. Set up the process:
   We will need separate processes for:
      - data supplied to the GUI
      - events + pipelines linked to event handlers
      - all other pipelines
   In order to set up the processes, you need to add certain configurations to your process definition, see [kafka-pipelines-processes.xml](server/kafka-pipelines-app/src/main/genesis/cfg/kafka-pipelines-processes.xml).
   You will also need to add the corresponding service definitions, see [kafka-pipelines-service-definitions.xml](server/kafka-pipelines-app/src/main/genesis/cfg/kafka-pipelines-service-definitions.xml)

5. Set up system definitions:
   You have the option to hard code your configurations into your pipelines script but for this example we show how to set up system definitions that will be called on inside the script, see [kafka-datapipelines-system-definition.kts](server/kafka-pipelines-app/src/main/genesis/cfg/kafka-pipelines-system-definition.kts). Using system definitions is more common, as it is likely the connection details for kafka will need to be different in each environment.

6. Set up event handler:
   In order to get the event to source the data for the pipeline, we need to create an implementation of the AbstractProgrammaticSource. See [ProgrammaticPriceSource](server/kafka-pipelines-app/src/main/kotlin/global/genesis/kafka/ProgrammaticPriceSource.kt).
   You can now write the event handler which will trigger the pipeline to publish to kafka, see [kafka-pipelines-eventhandler.kts](server/kafka-pipelines-app/src/main/genesis/scripts/kafka-pipelines-eventhandler.kts)

7. Write the pipelines scripts:
   You can now start writing your pipelines to write to and consume from the kafka topic that you've set up in the docker configuration:
      - see [kafka-pipelines-event-data-pipelines.kts](server/kafka-pipelines-app/src/main/genesis/scripts/kafka-pipelines-event-data-pipelines.kts) for the kafka sink pipeline.
      - see [kafka-pipelines-data-pipelines.kts](server/kafka-pipelines-app/src/main/genesis/scripts/kafka-pipelines-data-pipelines.kts) for kafka source pipeline.


## Running the app

To get this application running, as mentioned in the [docker-compose.yml](docker-compose.yml), first run `docker-compose up -d` in the directory of the docker-compose.yml to start up the docker kafka instance.
Then check the [Quick Start](https://docs.genesis.global/docs/develop/development-environments/) guide to start up the application itself.

If you need an introduction to the Genesis platform and its modules it's worth heading [here](https://docs.genesis.global/docs/develop/platform-overview/).


## Project Structure

This project contains **server** and **client** directories which contain the server and client code respectively.

### Server

The server code for this project can be found [here](./server/README.md).
It is built using a DSL-like definition based on the Kotlin language: GPAL.

When first opening the project, if you receive a notification from IntelliJ IDE detecting Gradle project select the option to 'Load as gradle project'.

### Web Client

The Web client for this project can be found [here](./client/README.md). It is built using Genesis's next
generation web development framework, which is based on Web Components.

## License

This is free and unencumbered software released into the public domain. For full terms, see [LICENSE](./LICENSE)

**NOTE** This project uses licensed components listed in the next section, thus licenses for those components are required during development.

## Licensed components
Genesis low-code platform
# Test results
BDD test results can be found [here](https://genesiscommunitysuccess.github.io/howto-kafka-pipelines/test-results)
