package com.durganmcbroom.jobs

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private class JobThrowable(
    val err: Any?
) : Throwable()

public class NewJobReference<E> {
    public fun earlyReturn(err: E): Nothing {
        throw JobThrowable(err)
    }
}

public fun <
        C : JobContext<*>,
        T,
        E> newJob(scope: C.(ref: NewJobReference<E>) -> T): Job<C, JobOutput<T, E>> {

    return Job { context ->
        val run = runCatching { context.scope(NewJobReference()) }

        val output = if (run.isSuccess) {
            JobOutput.Success(run.getOrNull()!!)
        } else {
            val exception = run.exceptionOrNull()
            val err = (exception as? JobThrowable)?.err ?: throw (exception
                ?: IllegalStateException("Job was not successful however there is also no error thrown."))
            JobOutput.Failure(err as E)
        }

        (context.orchestrator as? ExecutableJobOrchestrator<*>)?.orchestrate()

        output
    }
}

public fun <C : JobContext<*>, T : Any> newJobCatching(scope: C.(ref: NewJobReference<Throwable>) -> T): Job<C, JobOutput<T, Throwable>> {
    return newJob { ref ->
        val result = runCatching {
            scope(ref)
        }

        if (result.isFailure) ref.earlyReturn(result.exceptionOrNull()!!)
        else {
            result.getOrNull() ?: throw IllegalStateException("Job was successful however there is no result value!")
        }
    }
}

public fun <C : JobContext<*>, T> newWorkload(
    context: C,
    scope: C.() -> T
): T {
    return context.scope()
}

public fun <S : CompositionStub, C : JobContext<S>, O : JobOutput<*, *>> C.with(
    stub: S,
    job: Job<C, O>
): JobSupervisor<C, O> {
    return orchestrator.register(stub, job)
}

public fun <S : CompositionStub, C : JobContext<S>, T, E> C.with(
    stub: S,
    scope: C.(ref: NewJobReference<E>) -> T
): JobSupervisor<C, JobOutput<T, E>> {
    return orchestrator.register(stub, newJob(scope))
}