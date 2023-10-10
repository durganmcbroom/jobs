package com.durganmcbroom.jobs.logging.simple

import com.durganmcbroom.jobs.BasicJobElementFactory
import com.durganmcbroom.jobs.logging.LogLevel
import com.durganmcbroom.jobs.logging.Logger
import com.durganmcbroom.jobs.logging.LoggerFactory
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.LogRecord
import java.util.logging.Level as JavaLevel
import java.util.logging.Logger as JavaLogger

public fun SimpleLoggerFactory() : LoggerFactory = object : BasicJobElementFactory<Logger>(listOf(), {
    coroutineScope {
            val name = coroutineContext[CoroutineName]?.name
                ?: throw IllegalArgumentException("Cant find the job name! Make sure you add CoroutineName to the coroutine context.")

            SimpleLogger(SimpleLogger.createLogger(name))
        }
}), LoggerFactory {}
//    retur/n BasicJobElementFactory("SimpleLogger") {
//        coroutineScope {
//            val name = coroutineContext[CoroutineName]?.name
//                ?: throw IllegalArgumentException("Cant find the job name! Make sure you add CoroutineName to the coroutine context.")
//
//            SimpleLogger(SimpleLogger.createLogger(name))
//        }
//    }
//}

private class SimpleLogger (
    val realLogger: JavaLogger
) : Logger {
    companion object {
        fun createLogger(name: String) : JavaLogger {
            LogManager.getLogManager().reset()
            val rootLogger: JavaLogger = LogManager.getLogManager().getLogger("")

            val value = object : Handler() {
                override fun publish(record: LogRecord) {
                    val out = when (record.level) {
                        Level.SEVERE,
                        Level.WARNING -> System.err
                        else -> System.out
                    }

                    val zdt = ZonedDateTime.ofInstant(
                        record.instant, ZoneId.systemDefault()
                    )

                    val msg = String.format(
                        "%s @ %s - %s: \"%s\"",
                        zdt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")),
                        record.loggerName,
                        record.level.name,
                        record.message
                    )

                    out.println(msg)
                }
                override fun flush() {  }
                override fun close() {  }
            }

            rootLogger.addHandler(value)

            return JavaLogger.getLogger(name)
        }
    }

    override val name: String by realLogger::name

    private infix fun getLevel(logger: JavaLogger): LogLevel {
        fun mapLevel(level: JavaLevel): LogLevel = when (level) {
            JavaLevel.INFO -> LogLevel.INFO
            JavaLevel.FINE -> LogLevel.DEBUG
            JavaLevel.WARNING -> LogLevel.WARNING
            JavaLevel.FINER -> LogLevel.ERROR
            JavaLevel.SEVERE -> LogLevel.CRITICAL
            else -> LogLevel.INFO
        }

        return logger.level?.let(::mapLevel) ?: getLevel(logger.parent)
    }

    override var level: LogLevel = getLevel(realLogger)
        set(value) {
            field = value
            val dmLevelToJavaLevel = dmLevelToJavaLevel(value)
            realLogger.level = dmLevelToJavaLevel
            for (h in realLogger.handlers) {
                h.level = dmLevelToJavaLevel
            }
        }

    override fun log(level: LogLevel, msg: String) {
        val l = dmLevelToJavaLevel(level)

        realLogger.log(l, msg)
    }

    private fun dmLevelToJavaLevel(level: LogLevel): JavaLevel? = when (level) {
        LogLevel.INFO -> JavaLevel.INFO
        LogLevel.DEBUG -> JavaLevel.FINE
        LogLevel.WARNING -> JavaLevel.WARNING
        LogLevel.ERROR -> JavaLevel.FINER
        LogLevel.CRITICAL -> JavaLevel.SEVERE
    }
}

