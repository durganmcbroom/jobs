package com.durganmcbroom.jobs.coroutines

import kotlinx.coroutines.CoroutineScope

internal actual fun <T> runBlocking(block: suspend CoroutineScope.() -> T): T = kotlinx.coroutines.runBlocking(block = block)

