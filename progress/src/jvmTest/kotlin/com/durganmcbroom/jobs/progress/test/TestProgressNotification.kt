package com.durganmcbroom.jobs.progress.test

import com.durganmcbroom.jobs.*
import com.durganmcbroom.jobs.coroutines.CoroutineJobContext
import com.durganmcbroom.jobs.coroutines.CoroutineJobOrchestrator
import com.durganmcbroom.jobs.progress.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.newCoroutineContext
import org.junit.jupiter.api.Test
import kotlin.with

data class MyContext(
    override val progress: ProgressTracker,
    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : JobContext<MyContextStub>, CoroutineJobContext<MyContextStub>, ProgressingJobContext<MyContextStub, MyNotification> {
    override val orchestrator: JobOrchestrator<MyContextStub> = ProgressingJobOrchestrator(this,CoroutineJobOrchestrator(this))

    override fun compose(stub: MyContextStub): JobContext<MyContextStub> {
        return MyContext(progress.compose(), scope)
    }
}

data class MyContextStub(
    override val weight: Int
) : CompositionStub, ProgressingCompositionStub

data class MyNotification(
    val message: String
) : NotificationMetadata

class MyNotifier : ProgressNotifier {
    override fun notify(update: Progress, extra: String?) {
        println("Progress update: '${update.progress}%' with extra: '$extra'")
    }

    override fun compose(): ProgressNotifier {
        return MyNotifier()
    }
}

class TestProgressNotification {
    @Test
    fun `Test basic print notifications`() {
        val context = MyContext(
            WeightedProgressTracker(MyNotifier())
        )

        newWorkload(context) {
            progress.weight = 6

            with(MyContextStub(4)) { _ : NewJobReference<Nothing> ->
                progress.weight = 100

                blocking {
                    delay(500)
                    progress.status(Progress.from(0.5f)) {"From sub"}
                    delay(500)
                    progress.status(Progress.from(0.5f)) {"From sub"}
                }
            }
            progress.status(Progress.from(0.5f)) {"From super"}

            blocking {
                delay(1000)
            }
            progress.status(Progress.from(0.5f)){"From super"}
        }
    }
}