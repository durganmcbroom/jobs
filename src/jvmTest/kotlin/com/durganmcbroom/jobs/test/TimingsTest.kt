package com.durganmcbroom.jobs.test

import com.durganmcbroom.jobs.*
import com.durganmcbroom.jobs.test.TestFactoryTopoSort.Companion.newFactory
import com.durganmcbroom.jobs.topologicalSort
import kotlin.test.Test

class TimingsTest {
    private data class TimingsResult(
        val totalTime: Long,
        val iterations: Int
    ) {
        val timePerIteration : Double = totalTime.toDouble() / iterations
        fun printResult() {
            println("Total time: '$totalTime' nanos ('${totalTime / 1e9}' seconds)")
            println("Average time per iteration: '${totalTime / iterations}' nanos ('${totalTime / iterations / 1e9}' seconds)")
        }
    }

    private inline fun time(
        iterations: Int,
        block: () -> Unit
    ): TimingsResult {
        var totalTime = 0L
        repeat(iterations) { i ->
            val startTime = System.nanoTime()

            block()

            val endTime = System.nanoTime()
            val iterTime = endTime - startTime
            totalTime += iterTime
        }

        return TimingsResult(totalTime, iterations)
    }

    @Test
    fun `Test topological sort timings`() {
        val factories = ArrayList<JobFacetFactory>()

        factories.add(
            newFactory(
                TestFactoryTopoSort.TestKey("One"), listOf(
                    TestFactoryTopoSort.TestKey("Two"),
                    TestFactoryTopoSort.TestKey("Three")
                )
            )
        )
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Two"), listOf(TestFactoryTopoSort.TestKey("Four"))))
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Three"), listOf(TestFactoryTopoSort.TestKey("Five"))))
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Four"), listOf(TestFactoryTopoSort.TestKey("Three"))))
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Five"), listOf()))

        time(
            10000000
        ) {
            topologicalSort(factories)
        }.printResult()
    }


    @Test
    fun `Test full job composition`() {
        val factories = ArrayList<JobFacetFactory>()

        factories.add(
            newFactory(
                TestFactoryTopoSort.TestKey("One"), listOf(
                    TestFactoryTopoSort.TestKey("Two"),
                    TestFactoryTopoSort.TestKey("Three")
                )
            )
        )
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Two"), listOf(TestFactoryTopoSort.TestKey("Four"))))
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Three"), listOf(TestFactoryTopoSort.TestKey("Five"))))
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Four"), listOf(TestFactoryTopoSort.TestKey("Three"))))
        factories.add(newFactory(TestFactoryTopoSort.TestKey("Five"), listOf()))

        val context = factories.fold(EmptyJobContext as JobContext) { acc, it -> acc + it }

        val result1 = time(1000000) {
            job {
                Math.random()
            }.call(context)
        }
        result1.printResult()
        val result2 = time(1000000) {
            Math.random()
        }
        result2.printResult()
        println("Difference was: '${ result1.totalTime - result2.totalTime }' nanos total and '${result1.timePerIteration- result2.timePerIteration}' nanos per iteration")
    }
}