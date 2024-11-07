package com.durganmcbroom.jobs.logging.simple.test

import com.durganmcbroom.jobs.job
import com.durganmcbroom.jobs.launch
import com.durganmcbroom.jobs.logging.info
import com.durganmcbroom.jobs.logging.simple.SimpleLoggerFactory
import kotlin.test.Test

class TestSimpleLogging {
    @Test
    fun `Test simple logging`() {
        launch(SimpleLoggerFactory()) {
            job {
                info("Testing this")
            }
        }
    }
}