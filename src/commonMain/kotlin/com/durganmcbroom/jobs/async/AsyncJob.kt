package com.durganmcbroom.jobs.async

import com.durganmcbroom.jobs.JobContext
import com.durganmcbroom.jobs.JobDsl

@JobDsl
public interface AsyncJob<out T> {
    @JobDsl
    public suspend fun call(
        context: JobContext
    ): Result<T>
}