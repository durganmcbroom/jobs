package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.CompositionStub
import com.durganmcbroom.jobs.JobContext
import kotlin.jvm.JvmOverloads

public interface ProgressingJobContext<JobStub: CompositionStub, TrackerStub: CompositionStub> : JobContext<JobStub> {
    public val progress: ProgressTracker<TrackerStub>

    public fun status(update: Float, msg: String? = null) {
        progress.status(Progress.from(update), msg)
    }
}