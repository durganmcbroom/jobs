package com.durganmcbroom.jobs

@JobDsl
public sealed interface JobScope : ResultScope {
    public val context: JobContext

    @JobDsl
    public operator fun <T> Job<T>.invoke() : Result<T> = this@JobScope.join(this)

    @JobDsl
    public fun <T> join(job: Job<T>) : Result<T>
}