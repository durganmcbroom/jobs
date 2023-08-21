package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.CompositionStub

public class WeightedProgressTracker<T : CompositionStub>(
    private val notifier: ProgressNotifier<T>
) : ProgressTracker<T> {
    override var weight: Int = 1
        set(value) {
            totalWeight += -weight + value

            field = value
        }
    override var progress: Progress = Progress.from(0f)
        private set
    override val children: MutableList<ProgressTracker<T>> = ArrayList()

    private val listeners = ArrayList<ProgressListener>()
    private var totalWeight: Int = weight

    private fun updateProgress(update: Progress, pWeight: Int, notification: String?) {
        if (this.progress.finished) return

        val new = (pWeight.toFloat() / totalWeight.toFloat()) * update.progress
        this.progress = Progress.from(this.progress.progress + new)

        notifier.notify(this.progress, notification)
        listeners.forEach { it(Progress.from(new)) }
    }

    override fun registerChild(supervisor: ProgressingJobSupervisor<out ProgressingJobContext<*, T>, *>, weight: Int) {
        children.add(supervisor.context.progress)
        totalWeight += weight

        supervisor.addProgressListener {
            updateProgress(it, weight, null)
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

    override fun compose(stub: T): ProgressTracker<T> {
        val notifier1 = notifier.compose(stub)
        notifier1.notify(Progress.from(0f), "Starting...")
        return WeightedProgressTracker(notifier1)
    }
}