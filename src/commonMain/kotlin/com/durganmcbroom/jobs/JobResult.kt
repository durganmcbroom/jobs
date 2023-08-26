package com.durganmcbroom.jobs

public sealed class JobResult<out T, out E> {
    public fun wasSuccess() : Boolean = this is Success

    public fun wasFailure() : Boolean = this is Failure

    public fun orNull(): T? = (this as? Success<T>)?.output

    public fun failureOrNull(): E? = (this as? Failure<E>)?.output

    public fun <C> map(transform: (T) -> C) : JobResult<C, E> {
        @Suppress("unchecked_cast")
        val output = orNull() ?: return this as JobResult<C, E>

        return Success(transform(output))
    }
    public fun <C> mapFailure(transform: (E) -> C) : JobResult<T, C> {
        @Suppress("unchecked_cast")
        val output = failureOrNull() ?: return this as JobResult<T, C>

        return Failure(transform(output))
    }

    public data class Success<T>(
        val output: T
    ) : JobResult<T, Nothing>() {
        override fun toString(): String {
            return "Success($output)"
        }
    }

    public data class Failure<E>(
        val output: E
    ) : JobResult<Nothing, E>() {
        override fun toString(): String {
            return "Failure($output)"
        }
    }
}