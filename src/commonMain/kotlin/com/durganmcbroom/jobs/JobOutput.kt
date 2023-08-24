package com.durganmcbroom.jobs

import kotlin.jvm.JvmInline

public sealed class JobOutput<out T, out E> {
    public fun wasSuccess() : Boolean = this is Success

    public fun wasFailure() : Boolean = this is Failure

    public fun orNull(): T? = (this as? Success<T>)?.output

    public fun failureOrNull(): E? = (this as? Failure<E>)?.output

    public fun <C> map(transform: (T) -> C) : JobOutput<C, E> {
        @Suppress("unchecked_cast")
        val output = orNull() ?: return this as JobOutput<C, E>

        return Success(transform(output))
    }
    public fun <C> mapFailure(transform: (E) -> C) : JobOutput<T, C> {
        @Suppress("unchecked_cast")
        val output = failureOrNull() ?: return this as JobOutput<T, C>

        return Failure(transform(output))
    }

    public data class Success<T>(
        val output: T
    ) : JobOutput<T, Nothing>() {
        override fun toString(): String {
            return "Success($output)"
        }
    }

    public data class Failure<E>(
        val output: E
    ) : JobOutput<Nothing, E>() {
        override fun toString(): String {
            return "Failure($output)"
        }
    }
}