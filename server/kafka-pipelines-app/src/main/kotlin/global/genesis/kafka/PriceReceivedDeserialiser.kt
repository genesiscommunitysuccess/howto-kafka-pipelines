package global.genesis.kafka

import global.genesis.gen.dao.PriceReceived
import org.apache.kafka.common.serialization.Deserializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

// this is a simple kafka deserialiser which uses jackson to convert from the byte array to our Trade object
// the jackson dependency has been added to the build.gradle.kts of this app
class PriceReceivedDeserialiser : Deserializer<PriceReceived> {

    private val objectMapper = jacksonObjectMapper()

    override fun deserialize(topic: String?, data: ByteArray?): PriceReceived? {
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