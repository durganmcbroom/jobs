package com.durganmcbroom.jobs.logging.simple.test

import com.durganmcbroom.jobs.applyFactories
import com.durganmcbroom.jobs.logging.critical
import com.durganmcbroom.jobs.logging.info
import com.durganmcbroom.jobs.logging.simple.SimpleLoggerFactory
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class TestSimpleLogging {
    @Test
    fun `Test simple logging`() {
        runBlocking(SimpleLoggerFactory() + CoroutineName("First one")) {
            applyFactories<Unit, Nothing> {
                info("Hey how are you?")
                applyFactories<Unit, Nothing>(CoroutineName("Sub one")) {
                    critical("Uh oh!!! There was an error")
                    applyFactories<Unit, Nothing>(CoroutineName("Third one")) {
                        info("never mind, the third one says were fine")
                    }
                    info("Ok were back at here")
                }
            }
        }
    }
}