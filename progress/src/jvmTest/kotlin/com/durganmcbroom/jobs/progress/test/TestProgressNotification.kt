package com.durganmcbroom.jobs.progress.test

import com.durganmcbroom.jobs.holdElement
import com.durganmcbroom.jobs.job
import com.durganmcbroom.jobs.logging.simple.SimpleLogger
import com.durganmcbroom.jobs.logging.simple.newSimpleLogger
import com.durganmcbroom.jobs.newWorkload
import com.durganmcbroom.jobs.progress.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

//data class MyContext(
//    override val progress: ProgressTracker,
//    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
//) : JobContext<MyContextStub>, CoroutineJobContext<MyContextStub>, ProgressingJobContext<MyContextStub, MyNotification> {
//    override val orchestrator: JobOrchestrator<MyContextStub> = ProgressingJobOrchestrator(this,CoroutineJobOrchestrator(this))
//
//    override fun compose(stub: MyContextStub): JobContext<MyContextStub> {
//        return MyContext(progress.compose(), scope)
//    }
//}
//
//data class MyContextStub(
//    override val weight: Int
//) : CompositionStub, ProgressingCompositionStub

class MyNotifier(
    private val name: String
) : ProgressNotifier {
    override suspend fun notify(update: Progress, extra: String?) = coroutineScope {
        println("Progress update: '${update.progress}%' with extra: '$extra' from: '$name'")
    }

    override fun compose(old: ProgressNotifier): ProgressNotifier {
        return this
    }
}

class TestProgressNotification {
    fun context(name: String, influence: Int) : CoroutineContext {
        return CoroutineName(name) + WeightedProgressTracker(influence) + SimpleLogger(name) + holdElement(MyNotifier(""))
    }

    @Test
    fun `Test basic print notifications`() {
        runBlocking {
            job<Unit, Nothing>(context("The first one", 0)) {
                progress.weight = 6

                val job2 = job<String, Nothing>(context("Second job", 2)) {
                    delay(1000)
                    "This is from the second job"
                }

                val job3 = job<String, Nothing>(context("Third job?", 2)) {
                    delay(5000)
                    "This is from the third job"
                }

                println(job2.await().orNull()!!)
                println(job3.await().orNull()!!)

            }.join()
        }
    }
}