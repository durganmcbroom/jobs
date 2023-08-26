package com.durganmcbroom.jobs.progress.simple

import com.durganmcbroom.jobs.BasicJobElementFactory
import com.durganmcbroom.jobs.logging.LogLevel
import com.durganmcbroom.jobs.logging.LoggerFactory
import com.durganmcbroom.jobs.logging.logger
import com.durganmcbroom.jobs.progress.Progress
import com.durganmcbroom.jobs.progress.ProgressNotifier
import com.durganmcbroom.jobs.progress.ProgressNotifierFactory
import kotlinx.coroutines.coroutineScope
import kotlin.math.floor

public fun SimpleProgressNotifierFactory(): ProgressNotifierFactory =
    object : BasicJobElementFactory<ProgressNotifier>(listOf(LoggerFactory), {
         InternalSimpleProgressNotifier()
    }), ProgressNotifierFactory {}

private class InternalSimpleProgressNotifier: ProgressNotifier {
    override suspend fun notify(update: Progress, extra: String?) {
        coroutineScope {
            val message = extra?.let { " : $it" } ?: "."

            if (update.progress == 0f) {
                logger.log(LogLevel.INFO, "Job is starting$message")
            } else if (update.finished) {
                logger.log(LogLevel.INFO, "Job has finished$message")
            } else {
                logger.log(LogLevel.INFO, "Job is ${floor(update.progress * 100)}% done$message")
            }
        }
    }
}