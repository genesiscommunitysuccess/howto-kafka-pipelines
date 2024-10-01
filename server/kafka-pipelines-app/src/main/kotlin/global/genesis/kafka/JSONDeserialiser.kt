package global.genesis.kafka

import com.fasterxml.jackson.datatype.joda.JodaModule
import org.apache.kafka.common.serialization.Deserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

// this is a simple kafka deserialiser which uses jackson to convert from the byte array to a JSON object in the form Map<String, Any>
// the jackson dependency has been added to the build.gradle.kts of this app
class JSONDeserialiser : Deserializer<Map<String, Any>> {

    private val objectMapper = jacksonObjectMapper().apply {
        // as we are using Joda DateTime in the Genesis DAO object, the following is required in order to transform to/from that type
        registerModule(JodaModule())
    }

    override fun deserialize(topic: String?, data: ByteArray?): Map<String, Any>? {
        return if (data == null) {
            null
        } else {
            try {
                objectMapper.readValue(data)
            } catch (e: Exception) {
                throw RuntimeException("Could not deserialize data!", e)
            }
        }
    }
}