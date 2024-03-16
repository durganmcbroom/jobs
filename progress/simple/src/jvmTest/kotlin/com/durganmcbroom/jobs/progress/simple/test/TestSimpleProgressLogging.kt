package com.durganmcbroom.jobs.progress.simple.test

import com.durganmcbroom.jobs.JobName
import com.durganmcbroom.jobs.job
import com.durganmcbroom.jobs.launch
import com.durganmcbroom.jobs.logging.simple.SimpleLoggerFactory
import com.durganmcbroom.jobs.progress.JobWeight
import com.durganmcbroom.jobs.progress.WeightedProgressTrackerFactory
import com.durganmcbroom.jobs.progress.simple.SimpleProgressNotifierFactory
import com.durganmcbroom.jobs.progress.status
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
        launch(SimpleProgressNotifierFactory() + WeightedProgressTrackerFactory() + SimpleLoggerFactory()) {
            job(JobName("First job")) {
                println("Hi")

                job(JobName("Second job") + JobWeight(5)) {
                    println("Hey")
                    status(0.5f)
                    println("Hye fro here")
                }

                job(JobName("Third job") + JobWeight(2)) {
                    for (i in 0 until 100) {
                        status(0.01f)
                    }
                }

                Unit
            }
        }
    }
}