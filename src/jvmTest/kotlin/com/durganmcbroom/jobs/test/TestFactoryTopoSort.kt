package com.durganmcbroom.jobs.test

import com.durganmcbroom.jobs.Job
import com.durganmcbroom.jobs.JobElementFactory
import com.durganmcbroom.jobs.JobElementKey
import kotlin.test.Test

class TestFactoryTopoSort {
    data class TestKey(override val name: String) : JobElementKey<JobElementFactory>

    fun newFactory(key: TestKey, dependencies: List<JobElementKey<out JobElementFactory>>) : JobElementFactory {
        return object : JobElementFactory {
            override val dependencies: List<JobElementKey<out JobElementFactory>> = dependencies

            override fun <T, E> apply(job: Job<T, E>): Job<T, E> {
                TODO("Not yet implemented")
            }

            override val key: JobElementKey<JobElementFactory> = key
        }
    }

    private fun sort(list: List<JobElementFactory>) : List<JobElementFactory> {
       val method = Class.forName("com.durganmcbroom.jobs.Builders").getDeclaredMethod("topologicalSort", List::class.java)
        method.trySetAccessible()
        return method.invoke(null, list) as List<JobElementFactory>

    }

    @Test
    fun `Test the topological sort`() {
        val factories = ArrayList<JobElementFactory>()

        factories.add(newFactory(TestKey("One"), listOf(TestKey("Two"), TestKey("Three"))))
        factories.add(newFactory(TestKey("Two"), listOf(TestKey("Four"))))
        factories.add(newFactory(TestKey("Three"), listOf(TestKey("Five"))))
        factories.add(newFactory(TestKey("Four"), listOf(TestKey("Three"))))
        factories.add(newFactory(TestKey("Five"), listOf()))

        val sortedKeys = sort(factories)
            .map { it.key.name }

        println(sortedKeys)
        assert(
            sortedKeys ==
             listOf("Five", "Three", "Four", "Two", "One")
        )
    }
}