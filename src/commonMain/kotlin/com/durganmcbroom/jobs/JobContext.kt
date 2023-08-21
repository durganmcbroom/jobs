package com.durganmcbroom.jobs

public interface JobContext<T: CompositionStub> : Composable<JobContext<T>, T> {
    public val orchestrator: JobOrchestrator<T>

    override fun compose(stub: T) : JobContext<T>
}