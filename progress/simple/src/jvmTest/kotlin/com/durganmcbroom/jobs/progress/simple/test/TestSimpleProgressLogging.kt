package com.durganmcbroom.jobs.progress.simple.test

import com.durganmcbroom.jobs.*
import com.durganmcbroom.jobs.coroutines.CoroutineJobContext
import com.durganmcbroom.jobs.coroutines.CoroutineJobOrchestrator
import com.durganmcbroom.jobs.logging.Logger
import com.durganmcbroom.jobs.logging.simple.SimpleLogger
import com.durganmcbroom.jobs.progress.ProgressTracker
import com.durganmcbroom.jobs.progress.ProgressingCompositionStub
import com.durganmcbroom.jobs.progress.ProgressingJobOrchestrator
import com.durganmcbroom.jobs.progress.WeightedProgressTracker
import com.durganmcbroom.jobs.progress.simple.SimpleNotifierStub
import com.durganmcbroom.jobs.progress.simple.SimpleProgressJobContext
import com.durganmcbroom.jobs.progress.simple.SimpleProgressNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.test.Test

class MyContext(
    override val name: String,
    override val scope: CoroutineScope,
    override val logger: Logger,
    override val progress: ProgressTracker<SimpleNotifierStub> = WeightedProgressTracker(SimpleProgressNotifier(name,logger)),
) : JobContext<MyCompositionStub>, CoroutineJobContext<MyCompositionStub>, SimpleProgressJobContext<MyCompositionStub>,
    NamedJobContext<MyCompositionStub> {
    override val orchestrator: JobOrchestrator<MyCompositionStub> = ProgressingJobOrchestrator(this, CoroutineJobOrchestrator(this))

    constructor(name: String) : this(name, CoroutineScope(Dispatchers.Default), SimpleLogger(name))

    override fun compose(stub: MyCompositionStub): JobContext<MyCompositionStub> {
        val logger1 = SimpleLogger(stub.name)
        val notifierStub = SimpleNotifierStub(stub.name, logger1)
        return MyContext(stub.name, scope, logger, progress.compose(notifierStub))
    }
}

data class MyCompositionStub(
    override val name: String, override val weight: Int
) : NamedCompositionStub, ProgressingCompositionStub


class TestSimpleProgressLogging {
    @Test
    fun `Test simple progress logging`() {
        newWorkload(MyContext("First job")) {
            progress.weight = 6

            with(MyCompositionStub("Sub job 1", 4)) { ref : NewJobReference<Nothing> ->
                progress.weight = 5

                with(MyCompositionStub("Sub sub job 1", 5)) { ref : NewJobReference<Nothing> ->
                    blocking {
                        delay(1000)
                    }
                }

                status(0.5f, "Here")
                blocking {
                    delay(1000)
                }
            }

            blocking {
                delay(4000)
            }
            status(1f, "Finishing the last job")
        }
    }
}