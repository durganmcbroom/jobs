package com.durganmcbroom.jobs.logging

import com.durganmcbroom.jobs.*

public interface Logger : JobContext.Facet {
    override val key: JobContext.Key<Logger>
        get() = Logger
    public val name: String
    public var level: LogLevel

    public fun log(level: LogLevel, msg: String)

    public companion object : JobContext.Key<Logger> {
        override val name: String = "Logger"
    }
}

public interface LoggerFactory : JobFacetFactory {
    override val key: JobContext.Key<LoggerFactory>
        get() = LoggerFactory

    public companion object : JobContext.Key<LoggerFactory> {
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

public val JobScope.logger : Logger
    get() = facet(Logger)

public fun JobScope.info(msg: String): Unit = logger.log(LogLevel.INFO, msg)

public fun JobScope.debug(msg: String): Unit = logger.log(LogLevel.DEBUG, msg)

public fun JobScope.warning(msg: String): Unit = logger.log(LogLevel.WARNING, msg)

public fun JobScope.error(msg: String): Unit = logger.log(LogLevel.ERROR, msg)

public fun JobScope.critical(msg: String): Unit = logger.log(LogLevel.CRITICAL, msg)
