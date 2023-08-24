package com.durganmcbroom.jobs.progress.simple.test

import com.durganmcbroom.jobs.job
import com.durganmcbroom.jobs.logging.simple.SimpleLogger
import com.durganmcbroom.jobs.logging.simple.newSimpleLogger
import com.durganmcbroom.jobs.newWorkload
import com.durganmcbroom.jobs.progress.WeightedProgressTracker
import com.durganmcbroom.jobs.progress.simple.SimpleProgressNotifier
import kotlin.test.Test

//class MyContext(
//    override val name: String,
//    override val scope: CoroutineScope,
//    override val logger: Logger,
//    override val progress: ProgressTracker<SimpleNotifierStub> = WeightedProgressTracker(SimpleProgressNotifier(logger)),
//) : JobContext<MyCompositionStub>, CoroutineJobContext<MyCompositionStub>, SimpleProgressJobContext<MyCompositionStub>,
//    NamedJobContext<MyCompositionStub> {
//    override val orchestrator: JobOrchestrator<MyCompositionStub> = ProgressingJobOrchestrator(this, CoroutineJobOrchestrator(this))
//
//    constructor(name: String) : this(name, CoroutineScope(Dispatchers.Default), SimpleLogger(name))
//
//    override fun compose(stub: MyCompositionStub): JobContext<MyCompositionStub> {
//        val logger1 = SimpleLogger(stub.name)
//        val notifierStub = SimpleNotifierStub(logger1)
//        return MyContext(stub.name, scope, logger, progress.compose(notifierStub))
//    }
//}

//data class MyCompositionStub(
//    override val name: String, override val weight: Int
//) : NamedCompositionStub, ProgressingCompositionStub


class TestSimpleProgressLogging {
    @Test
    fun `Test simple progress logging`() {
        newWorkload(SimpleProgressNotifier()) {
            job<Unit, Nothing>(SimpleLogger("First job") + WeightedProgressTracker()) {
                println("Hi")
            }.join()
        }
    }
}