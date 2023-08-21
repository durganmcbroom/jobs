package com.durganmcbroom.jobs.coroutines

import com.durganmcbroom.jobs.CompositionStub
import com.durganmcbroom.jobs.JobContext
import kotlinx.coroutines.CoroutineScope

public interface CoroutineJobContext<T: CompositionStub> : JobContext<T> {
    public val scope: CoroutineScope

    public fun <T> blocking(block: suspend CoroutineScope.() -> T) : T = runBlocking {
        scope.block()
    }
}