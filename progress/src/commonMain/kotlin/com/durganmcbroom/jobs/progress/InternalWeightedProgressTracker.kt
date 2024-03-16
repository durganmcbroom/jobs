package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.*
import com.durganmcbroom.jobs.logging.Logger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

public data class JobWeight(
    val weight: Int
) : JobContext.Facet {
    override val key: JobContext.Key<JobWeight> = JobWeight

    public companion object : JobContext.Key<JobWeight> {
        override val name: String = "Job weight"
    }
}

public class WeightedProgressTrackerFactory : ProgressTrackerFactory {
    override val dependencies: List<JobContext.Key<out JobFacetFactory>> = listOf(
        ProgressNotifierFactory
    )


    override fun <T> apply(job: Job<T>, oldContext: JobContext): Job<T> = Job {
        val currentTracker = context[ProgressTracker]

        val childTracker = InternalWeightedProgressTracker(
            facet(ProgressNotifier),
            facet(Logger)
        )
        val childWeight = context[JobWeight]?.weight ?: 1

        currentTracker?.registerChild(childTracker, childWeight)

        withContext(childTracker) {
            status(0f)
            val output = job()
            progress.finish()
            output
        }
    }
}

private class InternalWeightedProgressTracker(
    private val progressNotifier: ProgressNotifier,
    private val logger: Logger
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

    private fun updateProgress(update: Progress, pWeight: Int, notification: String?) {
        if (this.progress.finished) return

        val new = (pWeight.toFloat() / totalWeight.toFloat()) * update.progress
        this.progress = Progress.from(this.progress.progress + new)

        progressNotifier.notify(this@InternalWeightedProgressTracker.progress, notification, logger)

        listeners.forEach { it(Progress.from(new)) }
    }


    override fun registerChild(child: ProgressTracker, influence: Int) {
        children.add(child)
        totalWeight += influence

        child.registerListener {
            if (it.progress != 0f) updateProgress(it, influence, null)
        }
    }

    override fun status(progress: Progress, msg: String?) {
        updateProgress(progress, this.weight, msg)
    }

    override fun registerListener(listener: ProgressListener) {
        listeners.add(listener)
    }

    override fun finish() {
        if (!progress.finished) updateProgress(Progress.from(1 - progress.progress), totalWeight, null)
    }
}