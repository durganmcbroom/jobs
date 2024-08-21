package com.durganmcbroom.jobs.async

import com.durganmcbroom.jobs.*


@JobDsl
public fun <T> AsyncJob(
    block: suspend JobScope.() -> Result<T>
): AsyncJob<T> = object : AsyncJob<T> {
    override suspend fun call(context: JobContext): Result<T> {
        val scope = DefaultJobScope(context)

        return scope.block()
    }
}

@JobDsl
public fun <T> asyncJob(
    context: JobContext = EmptyJobContext,
    block: suspend JobScope.() -> T
) : AsyncJob<T> {
    val job = AsyncJob {
        try {
            Result.success(block())
        } catch (t: Exception) {
            Result.failure(t)
        }
    }

    return applyFactoriesAsync(context, job)
}

private fun <T> applyFactoriesAsync(
    context: JobContext = EmptyJobContext,
    job: AsyncJob<T>
): AsyncJob<T> {
    return AsyncJob {
        val factories = sortFactories(this.context, context)

        factories
            .fold(job) { acc, it ->
                it.apply(acc, this.context)
            }.call(this.context.factories.fold(context) {acc, it -> acc + it})
    }
}

@JobDsl
public suspend fun <T> launchAsync(
    context: JobContext = EmptyJobContext,
    block: suspend JobScope.() -> T
): T {
    val scope = DefaultJobScope(context)
    return scope.block()
}