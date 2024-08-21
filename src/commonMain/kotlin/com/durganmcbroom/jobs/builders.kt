@file:JvmName("Builders")

package com.durganmcbroom.jobs

import com.durganmcbroom.jobs.async.AsyncJob

internal class DefaultJobScope(override val context: JobContext) : JobScope {
    override fun <T> join(job: Job<T>): Result<T> {
        return job.call(context)
    }

    override suspend fun <T> join(job: AsyncJob<T>): Result<T> {
        return job.call(context)
    }

    override fun <T> Result<T>.merge(): T {
        return getOrThrow()
    }
}

@JobDsl
public fun <T> Job(
    block: JobScope.() -> Result<T>
): Job<T> = object : Job<T> {
    override fun call(context: JobContext): Result<T> {
        val scope = DefaultJobScope(context)

        return scope.block()
    }
}


@JobDsl
public inline fun <T> SuccessfulJob(
    crossinline block: () -> T
): Job<T> {
    return Job {
        Result.success(block())
    }
}

@JobDsl
public inline fun FailingJob(
    crossinline block: () -> Throwable
): Job<Nothing> {
    return Job {
        Result.failure(block())
    }
}


// Does not apply factories, simply gives you a scope in which to make job invocations
@JobDsl
public fun <T> launch(
    context: JobContext = EmptyJobContext,
    block: JobScope.() -> T
): T {
    val scope = DefaultJobScope(context)
    return scope.block()
}

@JobDsl
public fun <T> job(
    context: JobContext = EmptyJobContext,
    block: JobScope.() -> T
): Job<T> {
    val job = Job {
        try {
            Result.success(block())
        } catch (t: Exception) {
            Result.failure(t)
        }
    }

    return applyFactories(context, job)
}

@JobDsl
public fun <T> JobScope.withContext(context: JobContext = EmptyJobContext, block: JobScope.() -> T): T {
    val scope = DefaultJobScope(this.context + context)
    return scope.block()
}

// Its very important that this method is not called from a Job created inside a
// facet factory. If this is the case and the factory consumes the context it was
// defined in a stack overflow will occur.
internal fun <T> applyFactories(
    context: JobContext = EmptyJobContext,
    job: Job<T>
): Job<T> {
    return Job {
        val factories = sortFactories(this.context, context)

        factories
            .fold(job) { acc, it ->
                it.apply(acc, this.context)
            }.call(this.context.factories.fold(context) { acc, it -> acc + it })
    }
}