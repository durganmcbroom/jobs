package com.durganmcbroom.jobs

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public interface JobElement<Self: JobElement<Self>> : Composable<Self> {
    public val key: JobElementKey<Self>
}

public interface JobElementKey<T: JobElement<T>> : CoroutineContext.Key<JobElementHolder<T>>

public interface JobElementHolder<T: JobElement<T>> : CoroutineContext.Element {
    public val inner: T
}

public interface JobLifecycleElement<Self: JobLifecycleElement<Self>> : JobElement<Self> {
    public fun <T, E> apply(job: Job<T, E>) : Job<T, E>
}

public expect fun <T: JobElement<T>> holdElement(curr: T) : JobElementHolder<T>

public fun <T: JobElement<T>> CoroutineScope.jobElement(key: JobElementKey<T>) : T {
    val holder = coroutineContext[key]

    return holder?.inner ?: throw IllegalArgumentException("Job element: '$key' is not installed in this coroutine context! Make sure to add it to your coroutine context to access it.")
}