package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.Composable
import com.durganmcbroom.jobs.CompositionStub
import kotlin.jvm.JvmStatic
import kotlin.math.abs
import kotlin.math.min

public typealias ProgressListener = (Progress) -> Unit

public interface ProgressTracker<T: CompositionStub> : Composable<ProgressTracker<T>, T> {
    public var weight: Int // Default should be 1
    public val progress: Progress
    public val children: List<ProgressTracker<T>>

    public fun registerChild(supervisor: ProgressingJobSupervisor<out ProgressingJobContext<*, T>, *>, weight: Int)

    public fun status(progress: Progress, msg: String?)

    public fun registerListener(listener: ProgressListener)

    public fun finish()
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
        public fun from(progress: Float) : Progress {
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