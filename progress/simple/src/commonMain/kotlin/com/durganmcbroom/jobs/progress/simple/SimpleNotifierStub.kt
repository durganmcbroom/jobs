package com.durganmcbroom.jobs.progress.simple

import com.durganmcbroom.jobs.CompositionStub
import com.durganmcbroom.jobs.logging.Logger

public data class SimpleNotifierStub(
    val name: String,
    val logger: Logger,
) : CompositionStub