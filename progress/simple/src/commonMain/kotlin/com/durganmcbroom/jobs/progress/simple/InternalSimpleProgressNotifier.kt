package com.durganmcbroom.jobs.progress.simple

import com.durganmcbroom.jobs.holdElement
import com.durganmcbroom.jobs.logging.LogLevel
import com.durganmcbroom.jobs.logging.logger
import com.durganmcbroom.jobs.progress.Progress
import com.durganmcbroom.jobs.progress.ProgressNotifier
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.math.floor

public fun SimpleProgressNotifier(): CoroutineContext = holdElement(InternalSimpleProgressNotifier())

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

    override fun compose(old: ProgressNotifier): ProgressNotifier {
        return this
    }
}