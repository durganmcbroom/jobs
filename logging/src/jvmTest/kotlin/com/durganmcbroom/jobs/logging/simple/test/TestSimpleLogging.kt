package com.durganmcbroom.jobs.logging.simple.test

import com.durganmcbroom.jobs.logging.critical
import com.durganmcbroom.jobs.logging.info
import com.durganmcbroom.jobs.logging.simple.newSimpleLogger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

//data class MyContext(
//    override val logger: Logger,
//    override val scope: CoroutineScope,
//) : JobContext<MyCompositionStub>, CoroutineJobContext<MyCompositionStub>, LoggingElement<MyCompositionStub> {
//    override val orchestrator: JobOrchestrator<MyCompositionStub> = CoroutineJobOrchestrator(this)
//
//    override fun compose(stub: MyCompositionStub): JobContext<MyCompositionStub> {
//        return MyContext(SimpleLogger(stub.name), scope)
//    }
//}
//
//data class MyCompositionStub(
//    val name: String
//) : CompositionStub

fun MyContext(name: String) : CoroutineContext {
    return newSimpleLogger(name)
}

class TestSimpleLogging {
    @Test
    fun `Test simple logging`() {
        runBlocking(MyContext("First one")) {
            info("Hey how are you?")
            launch(MyContext("Second one")) {
                critical("Uh oh!!! There was an error")
                launch(MyContext("Third one")) {
                    info("never mind, the third one says were fine")
                }
            }
        }

//        newWorkload(MyContext(SimpleLogger("First"), CoroutineScope(Dispatchers.Default))) {
//            logger.level = LogLevel.DEBUG
//
//            info("Can you see this?")
//            warning("Another test...")
//
//            error("AN ERROR OCCURRED")
//
//            critical("CRITICAL")
//
//            debug("A debug statement")
//        }
    }
}