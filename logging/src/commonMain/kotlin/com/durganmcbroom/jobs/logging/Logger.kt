package com.durganmcbroom.jobs.logging

import com.durganmcbroom.jobs.JobElement
import com.durganmcbroom.jobs.JobElementFactory
import com.durganmcbroom.jobs.JobElementKey
import com.durganmcbroom.jobs.jobElement
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public interface Logger : JobElement {
    override val key: JobElementKey<Logger>
        get() = Logger
    public val name: String
    public var level: LogLevel

    public fun log(level: LogLevel, msg: String)

    public companion object : JobElementKey<Logger> {
        override val name: String = "Logger"
    }
}

public interface LoggerFactory : JobElementFactory {
    override val key: JobElementKey<out JobElementFactory>
        get() = LoggerFactory

    public companion object : JobElementKey<LoggerFactory> {
        override val name: String = "Logger Factory"
    }
}

public enum class LogLevel {
    INFO,
    DEBUG,
    WARNING,
    ERROR,
    CRITICAL
}

public val CoroutineScope.logger : Logger
    get() = jobElement(Logger)

public fun CoroutineScope.info(msg: String): Unit = logger.log(LogLevel.INFO, msg)

public fun CoroutineScope.debug(msg: String): Unit = logger.log(LogLevel.DEBUG, msg)

public fun CoroutineScope.warning(msg: String): Unit = logger.log(LogLevel.WARNING, msg)

public fun CoroutineScope.error(msg: String): Unit = logger.log(LogLevel.ERROR, msg)

public fun CoroutineScope.critical(msg: String): Unit = logger.log(LogLevel.CRITICAL, msg)
