package com.durganmcbroom.jobs.coroutines

import kotlinx.coroutines.CoroutineScope

internal expect fun <T> runBlocking(block: suspend CoroutineScope.() -> T) : T