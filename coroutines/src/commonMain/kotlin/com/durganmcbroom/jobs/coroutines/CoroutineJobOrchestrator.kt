package com.durganmcbroom.jobs.coroutines

import com.durganmcbroom.jobs.*
import kotlinx.coroutines.async

public open class CoroutineJobOrchestrator<T : CompositionStub>(
    private val context: CoroutineJobContext<T>
) : JobOrchestrator<T> {
    override fun <C : JobContext<T>, O : JobOutput<*, *>> register(stub: T, job: Job<C, O>): JobSupervisor<C, O> {
        val c = context.compose(stub) as C

        val deferred = context.scope.async {
            job.run(c)
        }

        return DeferredSupervisor(deferred, c)
    }
}