package com.durganmcbroom.jobs.coroutines

import com.durganmcbroom.jobs.*
import kotlinx.coroutines.Deferred

public class DeferredSupervisor<C: JobContext<*>, O : JobOutput<*, *>>(
    private val deferred: Deferred<O>,
    override val context: C
) : JobSupervisor<C, O> {
    override val state: JobState
        get() = if (deferred.isCompleted) JobState.FINISHED
        else if (deferred.isActive) JobState.WORKING
        else JobState.NOT_STARTED

    private val listeners = ArrayList<JobStateListener>()
    init {
        notifyListeners(JobState.WORKING)
        deferred.invokeOnCompletion {
            notifyListeners(JobState.FINISHED)
        }
    }

    private fun notifyListeners(state: JobState) {
        listeners.forEach { it(state) }
    }

    override fun addListener(listener: JobStateListener) {
        listeners.add(listener)
    }

    override fun await(): O = runBlocking {
        deferred.await()
    }
}