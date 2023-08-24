package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.Job
import com.durganmcbroom.jobs.holdElement
import com.durganmcbroom.jobs.jobElement
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

public fun WeightedProgressTracker(
    influence: Int = 0
) : CoroutineContext {
    return holdElement(InternalWeightedProgressTracker(influence))
}

private class InternalWeightedProgressTracker(
    // The influence over a parent
    private val influence: Int,
) : ProgressTracker {
    override var weight: Int = 1
        set(value) {
            totalWeight += -weight + value

            field = value
        }
    override var progress: Progress = Progress.from(0f)
        private set
    override val children: MutableList<ProgressTracker> = ArrayList()

    private val listeners = ArrayList<ProgressListener>()
    private var totalWeight: Int = weight

    private suspend fun updateProgress(update: Progress, pWeight: Int, notification: String?)  {
        if (this.progress.finished) return

        val new = (pWeight.toFloat() / totalWeight.toFloat()) * update.progress
        this.progress = Progress.from(this.progress.progress + new)

        coroutineScope {
            jobElement(ProgressNotifier).notify(this@InternalWeightedProgressTracker.progress, notification)
        }
        listeners.forEach { it(Progress.from(new)) }
    }


    override fun registerChild(child: ProgressTracker, influence: Int) {
        children.add(child)
        totalWeight += influence

        child.registerListener {
            if (it.progress != 0f) updateProgress(it, influence, null)
        }
    }

    override suspend fun status(progress: Progress, msg: String?) {
        updateProgress(progress, this.weight, msg)
    }

    override fun registerListener(listener: ProgressListener) {
        listeners.add(listener)
    }

    override suspend fun finish() {
        if (!progress.finished) updateProgress(Progress.from(1 - progress.progress), totalWeight, null)
    }

    override fun <T, E> apply(job: Job<T, E>) : Job<T, E>  {
        return Job {
            status(Progress.from(0f), null)
            val output = job()
            finish()
            output
        }
    }

    override fun compose(old: ProgressTracker): InternalWeightedProgressTracker {
        old.registerChild(this, influence)
        return this
    }
}