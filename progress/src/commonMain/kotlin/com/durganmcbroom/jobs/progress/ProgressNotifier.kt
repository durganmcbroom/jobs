package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.JobElement
import com.durganmcbroom.jobs.JobElementKey
import kotlin.coroutines.CoroutineContext

public interface ProgressNotifier : JobElement<ProgressNotifier> {
    override val key: JobElementKey<ProgressNotifier>
        get() = ProgressNotifier

    public suspend fun notify(update: Progress, extra: String?)

    public companion object : JobElementKey<ProgressNotifier>
}