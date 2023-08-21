package com.durganmcbroom.jobs.logging

import com.durganmcbroom.jobs.CompositionStub
import com.durganmcbroom.jobs.JobContext

public interface LoggingContext<T: CompositionStub> : JobContext<T> {
    public val logger: Logger

    public fun info(msg: String): Unit = logger.log(LogLevel.INFO, msg)

    public fun debug(msg: String): Unit = logger.log(LogLevel.DEBUG, msg)

    public fun warning(msg: String) :Unit = logger.log(LogLevel.WARNING, msg)

    public fun error(msg: String) : Unit = logger.log(LogLevel.ERROR, msg)

    public fun critical(msg: String) : Unit = logger.log(LogLevel.CRITICAL, msg)
}