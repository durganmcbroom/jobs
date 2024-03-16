package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.min

public typealias ProgressListener = (Progress) -> Unit

public interface ProgressTrackerFactory : JobFacetFactory {
    override val key: JobContext.Key<ProgressTrackerFactory>
        get() = ProgressTrackerFactory

    public companion object : JobContext.Key<ProgressTrackerFactory> {
        override val name: String = "Progress Tracker factory"
    }
}

public interface ProgressTracker : JobContext.Facet {
    override val key: JobContext.Key<ProgressTracker> get() = ProgressTracker

    public var weight: Int // Default should be 1
    public val progress: Progress
    public val children: List<ProgressTracker>

    public fun registerChild(child: ProgressTracker, influence: Int)

    public fun status(progress: Progress, msg: String?)

    public fun registerListener(listener: ProgressListener)

    public fun finish()

    public companion object : JobContext.Key<ProgressTracker> {
        override val name: String = "Progress Tracker"
    }


}

public val JobScope.progress : ProgressTracker
    get() = facet(ProgressTracker)

public fun JobScope.status(progress: Float, msg: () -> String? = {null}): Unit {
    facet(ProgressTracker).status(Progress.from(progress), msg())
}

public class Progress private constructor(
    public val progress: Float
) {
    public val started: Boolean
        get() = progress != 0f
    public val finished: Boolean
        get() = progress == 1f

    public companion object {
        @JvmStatic
        public fun from(progress: Float): Progress {
            return Progress(min(abs(progress), 1f))
        }
    }

    override fun toString(): String {
        return "Progress(progress=$progress)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Progress

        return progress == other.progress
    }

    override fun hashCode(): Int {
        return progress.hashCode()
    }
}