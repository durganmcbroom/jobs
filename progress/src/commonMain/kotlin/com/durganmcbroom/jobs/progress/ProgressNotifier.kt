package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.BasicJobElementFactory
import com.durganmcbroom.jobs.JobElement
import com.durganmcbroom.jobs.JobElementFactory
import com.durganmcbroom.jobs.JobElementKey
import kotlin.coroutines.CoroutineContext

public interface ProgressNotifierFactory: JobElementFactory {
    override val key: JobElementKey<out JobElementFactory>
        get() = ProgressNotifierFactory

    public companion object : JobElementKey<ProgressNotifierFactory> {
        override val name: String = "Progress notifier factory"
    }
}

public interface ProgressNotifier : JobElement {
    override val key: JobElementKey<ProgressNotifier>
        get() = ProgressNotifier

    public suspend fun notify(update: Progress, extra: String?)

    public companion object : JobElementKey<ProgressNotifier> {
        override val name: String = "Progress Notifier"
    }
}