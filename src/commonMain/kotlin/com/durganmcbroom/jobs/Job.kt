package com.durganmcbroom.jobs

public interface Job<out T, out E> {
    public suspend operator fun invoke() : JobResult<T, E>
}