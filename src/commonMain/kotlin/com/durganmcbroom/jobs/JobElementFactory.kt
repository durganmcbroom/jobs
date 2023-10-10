package com.durganmcbroom.jobs

import kotlinx.coroutines.withContext

public interface JobElementFactory : JobElement {
    override val key: JobElementKey<out JobElementFactory>

    // Will apply factories in a topologically sorted list based on their dependencies and matching keys.
    public val dependencies: List<JobElementKey<out JobElementFactory>>

    public fun <T, E> apply(job: Job<T, E>): Job<T, E>
}

public abstract class BasicJobElementFactory<T : JobElement>(
    override val dependencies: List<JobElementKey<out JobElementFactory>> = ArrayList(),
    private val createElement: suspend () -> T,
) : JobElementFactory {

    override fun <T, E> apply(job: Job<T, E>): Job<T, E> = Job {
        withContext(createElement()) {
            job()
        }
    }

//    @JvmInline
//    private value class Key(
//        override val name: String
//    ) : JobElementKey<BasicJobElementFactory<*>>
}

