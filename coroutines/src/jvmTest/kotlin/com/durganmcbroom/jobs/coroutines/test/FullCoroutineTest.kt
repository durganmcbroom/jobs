package com.durganmcbroom.jobs.coroutines.test

import com.durganmcbroom.jobs.*
import com.durganmcbroom.jobs.coroutines.CoroutineJobContext
import com.durganmcbroom.jobs.coroutines.CoroutineJobOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.test.Test

private data class MyContext(
    val name: String,
    override val scope: CoroutineScope,
) : JobContext<MyContextStub>, CoroutineJobContext<MyContextStub> {
    override val orchestrator: JobOrchestrator<MyContextStub> = CoroutineJobOrchestrator(this)

    override fun compose(stub: MyContextStub): MyContext {
        return MyContext(stub.name, scope)
    }

    fun callThis() {

    }
}

private data class MyContextStub(
    val name: String
) : CompositionStub

class FullCoroutineTest {
    @Test
    fun `Test coroutine scheduling`() {

        newWorkload(MyContext("First", CoroutineScope(Dispatchers.Default))) {
            with(MyContextStub("Second")) { _: NewJobReference<Nothing> ->
                println("This ran second from: '$name'")

                blocking {
                    delay(100)
                    println("This ran third")
                }
            }

            println("This ran first from :'$name'")
            Thread.sleep(300) // need enough time for the second job to launch

            val supervisor = with(MyContextStub("Third"), otherJob())
            val output = supervisor.await()
            println(output.orNull() ?: output.failureOrNull())
        }
    }

    private fun otherJob() : Job<MyContext, JobOutput<String, Nothing>> = newJob {
        blocking {
            delay(200)

            "Work Complete! '$name' context completed it!"
        }
    }
}