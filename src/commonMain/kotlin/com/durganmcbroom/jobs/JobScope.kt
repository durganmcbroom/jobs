package com.durganmcbroom.jobs

import com.durganmcbroom.jobs.async.AsyncJob

@JobDsl
public sealed interface JobScope : ResultScope {
    public val context: JobContext

    @JobDsl
    public operator fun <T> Job<T>.invoke() : Result<T> = this@JobScope.join(this)

    @JobDsl
    public suspend operator fun <T> AsyncJob<T>.invoke() : Result<T> = this@JobScope.join(this)

    @JobDsl
    public fun <T> join(job: Job<T>) : Result<T>

    @JobDsl
    public suspend  fun <T> join(job: AsyncJob<T>) : Result<T>
}