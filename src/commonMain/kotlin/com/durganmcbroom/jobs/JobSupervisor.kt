package com.durganmcbroom.jobs

public typealias JobStateListener = (state: JobState) -> Unit

public interface JobSupervisor<C: JobContext<*>, O: JobOutput<*, *>> {
    public val context: C
    public val state: JobState

    public fun addListener(
        listener: JobStateListener
    )

    public fun await() : O

    public fun then(
        callback: (output: O) -> Unit
    ) {
        addListener {
            if (it == JobState.FINISHED) {
                callback(await())
            }
        }
    }
}