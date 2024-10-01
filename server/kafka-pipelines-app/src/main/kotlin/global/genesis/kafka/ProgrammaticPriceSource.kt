package global.genesis.kafka

import global.genesis.gen.dao.PricePublished
import global.genesis.pipeline.event.AbstractProgrammaticSource

// this is a realtime source which has an extra method called 'send', allowing us to send objects of the type we have initialised to the source flow
// see kafka-pipelines-eventhandler.kts to see how this is used
object ProgrammaticPriceSource : AbstractProgrammaticSource<PricePublished>()