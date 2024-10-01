plugins {
    kotlin("plugin.power-assert")
    id("global.genesis.bdd_automation") version "3.5.76"
}

dependencies {
    compileOnly(genesis("script-dependencies"))
    genesisGeneratedCode(withTestDependency = true)
    testImplementation(genesis("dbtest"))
    testImplementation(genesis("testsupport"))
    testImplementation(genesis("pal-eventhandler"))
    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("io.rest-assured:json-schema-validator:6.3.0")

    // this adds the dependency to the genesis pipeline module
    implementation(genesis("pal-datapipeline"))
    // this adds the dependency to the genesis kafka module which holds the Kafka Source that we will be using in our pipeline
    implementation("global.genesis:kafka-genesis:${findProperty("integrationsVersion")}")

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

// Genesis needs Groovy 3 and the BDD library Groovy 4, so the in-JVM Genesis tests run in their own suite
val genesisTest = sourceSets.create("genesisTest") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
    resources.srcDir("src/test/resources")
}

configurations {
    named(genesisTest.implementationConfigurationName) { extendsFrom(testImplementation.get()) }
    named(genesisTest.runtimeOnlyConfigurationName) { extendsFrom(testRuntimeOnly.get()) }
    named(genesisTest.compileClasspathConfigurationName) { exclude(group = "org.apache.groovy") }
    named(genesisTest.runtimeClasspathConfigurationName) { exclude(group = "org.apache.groovy") }
    testCompileClasspath { exclude(group = "org.codehaus.groovy") }
    testRuntimeClasspath { exclude(group = "org.codehaus.groovy") }
}

val genesisTestTask = tasks.register<Test>("genesisTest") {
    description = "Runs the in-JVM Genesis tests."
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = genesisTest.output.classesDirs
    classpath = genesisTest.runtimeClasspath
    shouldRunAfter(tasks.test)
    // Genesis sets its test JVM flags and system properties on test only
    val unitTest = tasks.test.get()
    jvmArgs(unitTest.jvmArgs)
    systemProperties(unitTest.systemProperties)
}

tasks.check { dependsOn(genesisTestTask) }

// Kafka.feature has no Cucumber runner, so test has nothing to discover
tasks.test { failOnNoDiscoveredTests = false }
