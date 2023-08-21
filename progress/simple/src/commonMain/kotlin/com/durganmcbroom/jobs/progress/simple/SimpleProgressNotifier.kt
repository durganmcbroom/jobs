package com.durganmcbroom.jobs.progress.simple

import com.durganmcbroom.jobs.logging.LogLevel
import com.durganmcbroom.jobs.logging.Logger
import com.durganmcbroom.jobs.progress.Progress
import com.durganmcbroom.jobs.progress.ProgressNotifier
import kotlin.math.floor

public class SimpleProgressNotifier(
    private val name: String,
    private val logger: Logger
) : ProgressNotifier<SimpleNotifierStub> {
    override fun notify(update: Progress, extra: String?) {
        val message = extra?.let { " : $it" } ?: "."

        if (update.progress == 0f) {
            logger.log(LogLevel.INFO, "Job '$name' is starting$message")
        } else if (update.finished) {
            logger.log(LogLevel.INFO, "Job '$name' has finished$message")
        } else {
            logger.log(LogLevel.INFO, "Job '$name' is ${floor(update.progress * 100)}% done$message")
        }
    }

    override fun compose(stub: SimpleNotifierStub): ProgressNotifier<SimpleNotifierStub> {
        return SimpleProgressNotifier(
            stub.name,
            stub.logger
        )
    }
}