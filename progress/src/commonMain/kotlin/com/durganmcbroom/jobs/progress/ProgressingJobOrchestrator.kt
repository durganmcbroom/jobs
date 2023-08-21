package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.*

public open class ProgressingJobOrchestrator<T : ProgressingCompositionStub>(
    private val context: ProgressingJobContext<T, *>,
    private val delegate: JobOrchestrator<T>
) : JobOrchestrator<T> {
    @Suppress("unchecked_cast")
    override fun <C : JobContext<T>, O : JobOutput<*, *>> register(stub: T, job: Job<C, O>): JobSupervisor<C, O> {
        val newJob = Job<C, O> {
            val r =job.run(it)
            (it as ProgressingJobContext<T, *>).progress.finish()
            r
        }

        val delegateSupervisor = delegate.register(stub, newJob)
        val supervisor =
            ProgressingJobSupervisor(delegateSupervisor, delegateSupervisor.context as ProgressingJobContext<T, *>) as ProgressingJobSupervisor<ProgressingJobContext<T, ProgressingCompositionStub>, O>
        (context.progress as ProgressTracker<ProgressingCompositionStub>).registerChild(supervisor, stub.weight)
        return supervisor as JobSupervisor<C, O>
    }
}