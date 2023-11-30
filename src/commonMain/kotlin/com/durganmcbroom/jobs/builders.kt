@file:JvmName("Builders")

package com.durganmcbroom.jobs

import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
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

    public fun <T> JobResult<T, E>.attempt(): T = when (this) {
        is JobResult.Failure<E> -> fail(output)
        is JobResult.Success<T> -> output
    }
}

public fun <T, E> Job(block: suspend CoroutineScope.() -> JobResult<T, E>): Job<T, E> = object : Job<T, E> {
    override suspend fun invoke(): JobResult<T, E> = coroutineScope(block)
}

public suspend fun <T, E> jobScope(
    block: suspend JobScope<E>.() -> T
) : JobResult<T, E> = coroutineScope {
    val result = runCatching {
        JobScope<E>(this@coroutineScope).block()
    }

    if (result.isSuccess) JobResult.Success(result.getOrNull()!!)
    else {
        val exception = result.exceptionOrNull()
        val err = (exception as? JobThrowable)?.err ?: throw (exception
            ?: IllegalStateException("Job was not successful however there is also no error thrown."))
        JobResult.Failure(err as E)
    }
}

public suspend fun <T, E> job(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend JobScope<E>.() -> T
): JobResult<T, E> {
    val job = Job {
        val result = runCatching {
            JobScope<E>(this@Job).block()
        }

        if (result.isSuccess) JobResult.Success(result.getOrNull()!!)
        else {
            val exception = result.exceptionOrNull()
            val err = (exception as? JobThrowable)?.err ?: throw (exception
                ?: IllegalStateException("Job was not successful however there is also no error thrown."))
            JobResult.Failure(err as E)
        }
    }

    return job(context, job)
}

public suspend fun <T, E> job(
    context: CoroutineContext = EmptyCoroutineContext,
    job: Job<T, E>
): JobResult<T, E> = withContext(context) {
    val factories = coroutineContext.fold(ArrayList<JobElementFactory>()) { acc, it: CoroutineContext.Element ->
        if (it is JobElementFactory) {
            acc.add(it)
        }
        acc
    }

    topologicalSort(factories)
        .reversed()
        .fold(job) {acc, it ->
        it.apply(acc)
    }()
}

public suspend fun <T> jobCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend JobScope<Throwable>.() -> T
): JobResult<T, Throwable> {
    val catchingJob = Job {
        val jobScope = JobScope<Throwable>(this)

        val result = runCatching {
            jobScope.block()
        }

        if (result.isSuccess) JobResult.Success(result.getOrNull()!!)
        else {
            val exception = result.exceptionOrNull()
            val err = ((exception as? JobThrowable)?.err as? Throwable) ?: exception
            ?: IllegalStateException("Job was not successful however there is also no error thrown.")
            JobResult.Failure(err)
        }
    }

    return job(context, catchingJob)
}

private fun topologicalSort(factories: List<JobElementFactory>) : List<JobElementFactory> {
    val factoryMap = factories.associateBy(JobElementFactory::key)

    val stack = ArrayList<JobElementFactory>(factories.size)
    val visited = HashSet<JobElementKey<*>>(factories.size)

    // Uses up more memory(a very small amount) but is very quick
    data class Trace(
        private val ordered: MutableList<JobElementKey<out JobElementFactory>>,
        private val set: MutableSet<JobElementKey<out JobElementFactory>>
    ) {
        fun push(key: JobElementKey<out JobElementFactory>) {
            ordered.add(key)
            if (!set.add(key)) {
                throw IllegalArgumentException(
                    "The following JobElementFactories have cyclic dependencies. The cyclic trace is as follows:\n${
                         ordered.joinToString(separator = " -> ") {it.name}
                    }"
                )
            }
        }

        fun pop() {
           set.remove(ordered.removeAt(ordered.size - 1))
        }
    }

    fun recursivelySort(factory: JobElementFactory, trace: Trace) {
        trace.push(factory.key)
        if (!visited.add(factory.key)) return

        for (dependency in factory.dependencies) {
            recursivelySort(factoryMap[dependency]
                ?: throw IllegalArgumentException("Factory: '${factory.key.name}' " +
                        "required other type: '${dependency.name}' to be installed in " +
                        "the current context and it was not found!"), trace)
        }

        trace.pop()
        stack.add(factory)
    }

    for (factory in factories) {
        recursivelySort(factory, Trace(
            ArrayList(), HashSet()
        ))
    }

    return stack
}