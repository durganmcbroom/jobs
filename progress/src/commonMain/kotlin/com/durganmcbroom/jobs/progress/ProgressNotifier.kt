package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.*
import com.durganmcbroom.jobs.logging.Logger

public interface ProgressNotifierFactory: JobFacetFactory {
    override val key: JobContext.Key<ProgressNotifierFactory>
        get() = ProgressNotifierFactory

    public companion object : JobContext.Key<ProgressNotifierFactory> {
        override val name: String = "Progress notifier factory"
    }
}

public interface ProgressNotifier : JobContext.Facet {
    override val key: JobContext.Key<ProgressNotifier>
        get() = ProgressNotifier

    public fun notify(update: Progress, extra: String?, logger: Logger)

    public companion object : JobContext.Key<ProgressNotifier> {
        override val name: String = "Progress Notifier"
    }
}