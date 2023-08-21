package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.JobOutput
import com.durganmcbroom.jobs.JobState
import com.durganmcbroom.jobs.JobStateListener
import com.durganmcbroom.jobs.JobSupervisor

public class ProgressingJobSupervisor<C: ProgressingJobContext<*, *>, O: JobOutput<*, *>>(
    private val delegate: JobSupervisor<*, O>,
    override val context: C
) : JobSupervisor<C, O> {
    override val state: JobState by delegate::state
    public val progress:Progress
        get() = context.progress.progress

    override fun addListener(listener: JobStateListener) {
        delegate.addListener(listener)
    }

    override fun await(): O {
        return delegate.await()
    }

    public fun addProgressListener(listener: ProgressListener) {
        context.progress.registerListener(listener)
    }
}