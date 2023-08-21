package com.durganmcbroom.jobs

public interface JobOrchestrator<T: CompositionStub> {
    public fun <C: JobContext<T>, O: JobOutput<*, *>> register(stub: T, job: Job<C, O>) : JobSupervisor<C, O>
}

public interface ExecutableJobOrchestrator<T: CompositionStub> : JobOrchestrator<T> {
    public fun orchestrate()
}