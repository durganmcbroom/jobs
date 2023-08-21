package com.durganmcbroom.jobs.logging.simple.test

import com.durganmcbroom.jobs.CompositionStub
import com.durganmcbroom.jobs.JobContext
import com.durganmcbroom.jobs.JobOrchestrator
import com.durganmcbroom.jobs.coroutines.CoroutineJobContext
import com.durganmcbroom.jobs.coroutines.CoroutineJobOrchestrator
import com.durganmcbroom.jobs.logging.LogLevel
import com.durganmcbroom.jobs.logging.Logger
import com.durganmcbroom.jobs.logging.LoggingContext
import com.durganmcbroom.jobs.logging.simple.SimpleLogger
import com.durganmcbroom.jobs.newWorkload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.Test

data class MyContext(
    override val logger: Logger,
    override val scope: CoroutineScope,
) : JobContext<MyCompositionStub>, CoroutineJobContext<MyCompositionStub>, LoggingContext<MyCompositionStub> {
    override val orchestrator: JobOrchestrator<MyCompositionStub> = CoroutineJobOrchestrator(this)

    override fun compose(stub: MyCompositionStub): JobContext<MyCompositionStub> {
        return MyContext(SimpleLogger(stub.name), scope)
    }
}

data class MyCompositionStub(
    val name: String
) : CompositionStub

class TestSimpleLogging {
    @Test
    fun `Test simple logging`() {
        newWorkload(MyContext(SimpleLogger("First"), CoroutineScope(Dispatchers.Default))) {
            logger.level = LogLevel.DEBUG

            info("Can you see this?")
            warning("Another test...")

            error("AN ERROR OCCURRED")

            critical("CRITICAL")

            debug("A debug statement")
        }
    }
}