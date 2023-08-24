package com.durganmcbroom.jobs

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

private class JobThrowable(
    val err: Any?
) : Throwable()

public class JobScope<E> internal constructor(
    private val delegate: CoroutineScope
) : CoroutineScope by delegate {
    public fun fail(err: E): Nothing {
        throw JobThrowable(err)
    }

    public fun <T> JobOutput<T, E>.attempt() : T = when (this) {
        is JobOutput.Failure<E> -> fail(output)
        is JobOutput.Success<T> -> output
    }
}

public fun <T, E> Job(block: suspend CoroutineScope.() -> JobOutput<T, E>): Job<T, E> = object : Job<T, E> {
    override suspend fun invoke(): JobOutput<T, E> = coroutineScope(block)
}

public suspend fun <T, E> job(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend JobScope<E>.() -> T
): Deferred<JobOutput<T, E>> {
    val job = Job {
        val result = runCatching {
            JobScope<E>(this@Job).block()
        }

        if (result.isSuccess) JobOutput.Success(result.getOrNull()!!)
        else {
            val exception = result.exceptionOrNull()
            val err = (exception as? JobThrowable)?.err ?: throw (exception
                ?: IllegalStateException("Job was not successful however there is also no error thrown."))
            JobOutput.Failure(err as E)
        }
    }

    return job(context, job)
}

public suspend fun <T, E> job(
    context: CoroutineContext = EmptyCoroutineContext,
    job: Job<T, E>
): Deferred<JobOutput<T, E>> = coroutineScope {
    val appliedJob = context.fold(job) { acc, it ->
        if (it is JobElementHolder<*>) (it.inner as? JobLifecycleElement<*>)?.apply(acc) ?: acc else acc
    }

    async(context) {
        appliedJob()
    }
}

public suspend fun <T> jobCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend JobScope<Throwable>.() -> T
): Deferred<JobOutput<T, Throwable>> {
    val catchingJob = Job {
        val jobScope = JobScope<Throwable>(this)

        val result = runCatching {
            jobScope.block()
        }

        if (result.isSuccess) JobOutput.Success(result.getOrNull()!!)
        else {
            val exception = result.exceptionOrNull()
            val err = ((exception as? JobThrowable)?.err as? Throwable) ?: exception
            ?: IllegalStateException("Job was not successful however there is also no error thrown.")
            JobOutput.Failure(err)
        }
    }

    return job(context, catchingJob)
}