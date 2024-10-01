plugins {
    id("global.genesis.allure") version "1.0.0"
}

dependencies {
    compileOnly(genesis("script-dependencies"))
    genesisGeneratedCode(withTestDependency = true)
    testImplementation(genesis("dbtest"))
    testImplementation(genesis("testsupport"))
    testImplementation(genesis("pal-eventhandler"))
    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("io.rest-assured:json-schema-validator:latest.release")

    // this adds the dependency to the genesis pipeline module
    implementation(genesis("pal-datapipeline"))
    // this adds the dependency to the genesis kafka module which holds the Kafka Source that we will be using in our pipeline
    implementation("global.genesis:kafka-genesis:${properties["platformIntegrationVersion"]}")
    // this adds the dependency for the kafka library that we will use to initialise the deserialisers and allow us to specify the type our operator will take in
    implementation("org.apache.kafka:kafka-clients:3.8.0")

    // OPTIONAL FOR CUSTOM TRADE deserialiser
    // this adds the dependency for the object mapper that's used to convert the genesis dao to a json object for the deserialization
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
}

description = "kafka-pipelines-app"

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources", "src/main/genesis")
        }
    }
}
