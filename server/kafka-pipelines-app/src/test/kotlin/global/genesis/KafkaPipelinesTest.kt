package global.genesis

import com.google.inject.Inject
import global.genesis.db.rx.entity.multi.SyncEntityDb
import global.genesis.gen.config.tables.PRICE_PUBLISHED
import global.genesis.gen.config.tables.PRICE_RECEIVED
import global.genesis.gen.dao.PricePublished
import global.genesis.gen.dao.enums.kafka_pipelines.price_published.Side
import global.genesis.message.core.event.EventReply
import global.genesis.pipeline.PipelineManager
import global.genesis.testsupport.client.eventhandler.EventClientSync
import global.genesis.testsupport.jupiter.GenesisJunit
import global.genesis.testsupport.jupiter.PackageScan
import global.genesis.testsupport.jupiter.ScriptFile
import global.genesis.testsupport.jupiter.SysDefOverwrite
import global.genesis.testsupport.jupiter.assertedCast
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime

/**
 * The following is an integration test for this application.
 * It is dependent on a kafka instance being available as per the configuration in docker-compose.yml located at the root of this project.
 * Please see this file for more information on the container and how to start it up.
 * Without the zookeeper + kafka containers running, this test will not pass.
 */
@ExtendWith(GenesisJunit::class)
@PackageScan(PipelineManager::class)
@ScriptFile("kafka-pipelines-data-pipelines.kts")
@ScriptFile("kafka-pipelines-event-data-pipelines.kts")
@ScriptFile("kafka-pipelines-eventhandler.kts")
@SysDefOverwrite("BOOTSTRAP_SERVER", "localhost:9092")
@SysDefOverwrite("CONSUMER_GROUP_ID", "test-consumer")
@SysDefOverwrite("PRODUCER_CLIENT_ID", "test-producer")
@SysDefOverwrite("KAFKA_TOPIC", "price-topic")
@SysDefOverwrite("KAFKA_SECURITY_CONFIG", "PLAINTEXT")
class KafkaPipelinesTest {
    @Inject
    lateinit var db: SyncEntityDb

    @Inject
    lateinit var client: EventClientSync

    @Test
    fun `test when record sent to EVENT_PRICE_PUBLISH, kafka picks this up and inserts into db`() = runBlocking {
        delay(10000)

        val record = PricePublished(
            isin = "my-isin",
            priceDatetime = DateTime(),
            bondPrice = 0.0,
            maxQuantity = 0,
            indicative = true,
            priceDate = DateTime(),
            side = Side.BUY
        )
        client.sendEvent(
            details =record,
            messageType = "EVENT_PRICE_PUBLISH"
        ).assertedCast<EventReply.EventAck>()

        // assert insert into db PricePublished
        val pricesPublished = db.getBulk(PRICE_PUBLISHED).toList()
        assertEquals(1, pricesPublished.size)
        assertEquals(record.isin, pricesPublished[0].isin)
        assertEquals(record.priceDatetime, pricesPublished[0].priceDatetime)

        delay(10000)

        // assert insert into db PriceReceived
        val pricesReceived = db.getBulk(PRICE_RECEIVED).toList()
        assertEquals(1, pricesReceived.size)
        assertEquals(record.isin, pricesReceived[0].isin)
        assertEquals(record.priceDatetime, pricesReceived[0].priceDatetime)
    }
}
