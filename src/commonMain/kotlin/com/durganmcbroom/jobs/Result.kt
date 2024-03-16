package com.durganmcbroom.jobs

@JobDsl
public sealed interface ResultScope {
    @JobDsl
    public fun <T> Result<T>.merge() : T {
        return getOrThrow()
    }
}

private data object DefaultResultScope : ResultScope

public fun <T> result(block: ResultScope.() -> T) : Result<T> {
    return try {
       Result.success(DefaultResultScope.block())
    } catch (e: Exception) {
        Result.failure(e)
    }
}

public inline fun <T> Result<T>.mapException(transformer: (Throwable) -> Throwable) : Result<T> {
    return exceptionOrNull()?.let {
       Result.failure(transformer(it))
    } ?: this
}