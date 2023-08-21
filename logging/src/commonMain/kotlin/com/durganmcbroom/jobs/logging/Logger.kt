package com.durganmcbroom.jobs.logging

public interface Logger {
    public val name: String
    public var level: LogLevel

    public fun log(level: LogLevel, msg: String)
}

public enum class LogLevel {
    INFO,
    DEBUG,
    WARNING,
    ERROR,
    CRITICAL
}
