package com.durganmcbroom.jobs.test

import com.durganmcbroom.jobs.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test

private class MyJobElement(
    val name: String,
    val parent: MyJobElement?
) : JobElement<MyJobElement> {
    override val key: JobElementKey<MyJobElement> = MyJobElement

    override fun compose(old: MyJobElement): MyJobElement {
        return MyJobElement(name, old)
    }

     companion object : JobElementKey<MyJobElement>
}

private fun MyJobElement(name: String) = holdElement(MyJobElement(name, null))

class TestJobElementComposition {
    @Test
    fun `Test element composes properly`() {
        runBlocking {
            job<Unit, Nothing>(MyJobElement("First")) {
                job<Unit, Nothing>(MyJobElement("Second")) {
                    job<String, String>(MyJobElement("Third")) {
                        val elem = coroutineContext[MyJobElement]?.inner
                        assert(elem?.name == "Third")
                        assert(elem?.parent?.name == "Second")
                        assert(elem?.parent?.parent?.name == "First")
                        println("The third one")

                        "Hey"
                    }.join()

                    delay(500)
                    val elem = coroutineContext[MyJobElement]?.inner

                    assert(elem?.name == "Second")
                    println("The second one")
                }.join()
                delay(1000)
                val elem = coroutineContext[MyJobElement]?.inner

                assert(elem?.name == "First")
                println("The first one")
            }.join()
        }
    }

    @Test
    fun `Test job building returns correctly`() {
//        runBlocking {
//            val job1 = async {
//                newJob<String, Nothing> {
//                    delay(100)
//                    ""
//                }
//            }
//
//            val job2 = async {
//                newJob<String, String> {
//                    delay(1000)
//                    shift("Oh no... we failed!!")
//                }
//            }
//
//            assert(job1.await().wasSuccess())
//            assert(job2.await().wasFailure())
//        }
    }
}