package com.durganmcbroom.jobs.progress.test

import com.durganmcbroom.jobs.JobName
import com.durganmcbroom.jobs.job
import com.durganmcbroom.jobs.launch
import com.durganmcbroom.jobs.logging.LogLevel
import com.durganmcbroom.jobs.logging.Logger
import com.durganmcbroom.jobs.logging.simple.SimpleLoggerFactory
import com.durganmcbroom.jobs.progress.Progress
import com.durganmcbroom.jobs.progress.ProgressNotifier
import com.durganmcbroom.jobs.progress.WeightedProgressTrackerFactory
import com.durganmcbroom.jobs.progress.simple.SimpleProgressNotifierFactory
import org.junit.jupiter.api.Test

class MyNotifier(
    private val name: String
) : ProgressNotifier {
    override fun notify(update: Progress, extra: String?, logger: Logger) {
        logger.log(LogLevel.INFO, "Progress update: '${update.progress}%' with extra: '$extra' from: '$name'")
    }
}

class TestProgressNotification {
    @Test
    fun `Test basic print notifications`() {
        launch(SimpleProgressNotifierFactory() + WeightedProgressTrackerFactory() + SimpleLoggerFactory()) {
            job {
                val job2 = job(JobName("Second job")) {
                    "This is from the second job"
                }().merge()

                val job3 = job(JobName("Third job?")) {
                    "This is from the third job"
                }().merge()

                println(job2)
                println(job3)
            }
        }
    }
}