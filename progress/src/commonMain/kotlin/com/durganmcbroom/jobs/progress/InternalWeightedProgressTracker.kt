package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

public data class JobWeight(
    val weight: Int
) : JobElement {
    override val key: JobElementKey<JobWeight> = JobWeight

    public companion object : JobElementKey<JobWeight> {
        override val name: String = "Job weight"
    }
}

public class WeightedProgressTrackerFactory: ProgressTrackerFactory {
    override val dependencies: List<JobElementKey<out JobElementFactory>> = listOf(
        ProgressNotifierFactory
    )

    override fun <T, E> apply(job: Job<T, E>): Job<T, E> {
        return Job {
            val currentTracker = coroutineContext[ProgressTracker]

            val childTracker = InternalWeightedProgressTracker(coroutineContext)
            val childWeight = coroutineContext[JobWeight]?.weight ?: 1

            currentTracker?.registerChild(childTracker, childWeight)

            withContext(childTracker) {
                status(0f)
                val output = job()
                progress.finish()
                output
            }
        }
    }
}

private class InternalWeightedProgressTracker(
    // The influence over a parent
    private val context: CoroutineContext
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

    private suspend fun updateProgress(update: Progress, pWeight: Int, notification: String?) {
        if (this.progress.finished) return

        val new = (pWeight.toFloat() / totalWeight.toFloat()) * update.progress
        this.progress = Progress.from(this.progress.progress + new)
        withContext(context) {
            jobElement(ProgressNotifier).notify(this@InternalWeightedProgressTracker.progress, notification)

            listeners.forEach { it(Progress.from(new)) }
        }
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
}