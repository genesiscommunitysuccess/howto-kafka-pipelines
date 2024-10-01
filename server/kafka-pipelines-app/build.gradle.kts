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
    implementation("global.genesis:kafka-genesis:${properties["integrationsVersion"]}")

    // this adds the dependency for the object mapper that's used to convert the Genesis DAO to a JSON object for the serialisation/deserialisation
    // note: these are only needed if you're using jackson as the object mapper in your serialiser/deserialiser and are not part of the required dependencies for using kaka pipelines within Genesis
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    // as we are using Joda DateTime in the Genesis DAO object, the following is required in order to transform to/from that type
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.13.0")
}

description = "kafka-pipelines-app"

sourceSets {
    main {
        resources {
            srcDirs("src/main/resources", "src/main/genesis")
        }
    }
}
