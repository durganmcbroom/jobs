package com.durganmcbroom.jobs.progress.simple

import com.durganmcbroom.jobs.BasicJobElementFactory
import com.durganmcbroom.jobs.logging.LogLevel
import com.durganmcbroom.jobs.logging.LoggerFactory
import com.durganmcbroom.jobs.logging.logger
import com.durganmcbroom.jobs.progress.Progress
import com.durganmcbroom.jobs.progress.ProgressNotifier
import com.durganmcbroom.jobs.progress.ProgressNotifierFactory
import kotlinx.coroutines.coroutineScope
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.pow

public fun SimpleProgressNotifierFactory(
    precision: Int = 4,
    interval: Float = 0f
): ProgressNotifierFactory =
    object : BasicJobElementFactory<ProgressNotifier>(listOf(LoggerFactory), {
        InternalSimpleProgressNotifier(precision, interval)
    }), ProgressNotifierFactory {}

private class InternalSimpleProgressNotifier(
    private val precision: Int,
    private val interval: Float
) : ProgressNotifier {
    private var lastUpdate = 0.0f

    override suspend fun notify(update: Progress, extra: String?) {
        coroutineScope {
            if (lastUpdate + interval > update.progress) return@coroutineScope
            lastUpdate = update.progress

            val message = extra?.let { " : $it" } ?: "."

            if (update.progress == 0f) {
                logger.log(LogLevel.INFO, "Job is starting$message")
            } else if (update.finished) {
                logger.log(LogLevel.INFO, "Job has finished$message")
            } else {
                logger.log(
                    LogLevel.INFO,
                    "Job is ${floor(10.0.pow(precision) * update.progress * 100) / 10.0.pow(precision)}% done$message"
                )
            }
        }
    }
}