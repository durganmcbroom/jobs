package com.durganmcbroom.jobs

@JobDsl
public interface Job<out T> {
    @JobDsl
    public fun call(
        context: JobContext
    ): Result<T>
}

public inline fun <T, K> Job<T>.map(crossinline transformer: JobScope.(T) -> K): Job<K> = Job {
    this@map().map { transformer(it) }
}

public inline fun <T> Job<T>.mapException(crossinline transformer: (Throwable) -> Throwable): Job<T> = Job {
    val r = this@mapException()

    if (r.isFailure) Result.failure(transformer(r.exceptionOrNull()!!))
    else r
}