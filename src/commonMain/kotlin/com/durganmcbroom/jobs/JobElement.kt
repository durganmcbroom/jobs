package com.durganmcbroom.jobs

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public interface JobElement : CoroutineContext.Element {
    override val key: JobElementKey<*>
}

public interface JobElementKey<T: JobElement> : CoroutineContext.Key<T> {
    public val name: String
}

//public interface JobElementHolder<T: JobElement<T>> : CoroutineContext.Element {
//    public val inner: T
//}

//public interface JobLifecycleElement<Self: JobLifecycleElement<Self>> : JobElement<Self> {
//    public fun <T, E> apply(job: Job<T, E>) : Job<T, E>
//}

//public expect fun <T: JobElement<T>> holdElement(curr: T) : JobElementHolder<T>

public fun <T: JobElement> CoroutineScope.jobElement(key: JobElementKey<T>) : T {
    val holder = coroutineContext[key]

    return holder ?: throw IllegalArgumentException("Job element: '$key' is not installed in this coroutine context! Make sure to add it to your coroutine context to access it.")
}