package global.genesis.kafka

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.common.serialization.Serializer

// this is a simple kafka serialiser which uses jackson to convert from a JSON object in the form Map<String, Any> to a byte array for kafka to process
// the jackson dependency has been added to the build.gradle.kts of this app
class JSONSerialiser : Serializer<Map<String, Any>> {

    private val objectMapper = jacksonObjectMapper().apply {
        // as we are using Joda DateTime in the Genesis DAO object, the following is required in order to transform to/from that type
        registerModule(JodaModule())
    }

    override fun serialize(topic: String, data: Map<String, Any>?): ByteArray? {
        return if (data == null) {
            null
        } else {
            try {
                objectMapper.writeValueAsBytes(data)
            } catch (e: Exception) {
                throw RuntimeException("Error serializing Map to JSON", e)
            }
        }
    }
}