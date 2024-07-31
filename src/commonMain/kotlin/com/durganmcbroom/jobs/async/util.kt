package com.durganmcbroom.jobs.async

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

public suspend fun <T, R> Collection<T>.mapAsync(transform: suspend (T) -> R): List<Deferred<R>> = coroutineScope {
    map { async { transform(it) } }
}

public suspend fun <T, R, C: MutableCollection<Deferred<R>>> Collection<T>.mapAsyncTo(
    collection: C,
    transform: suspend (T) -> R
): C = coroutineScope {
    mapTo(collection) { async { transform(it) } }
}