package com.durganmcbroom.jobs

internal fun topologicalSort(factories: List<JobFacetFactory>): List<JobFacetFactory> {
    if (factories.size <= 1) return factories

    val factoryMap = factories.associateBy(JobFacetFactory::key)

    val stack = ArrayList<JobFacetFactory>(factories.size)
    val visited = HashSet<JobContext.Key<*>>(factories.size)

    // Uses up more memory(a very small amount) but is very quick
    data class Trace(
        private val ordered: MutableList<JobContext.Key<*>>,
        private val set: MutableSet<JobContext.Key<*>>
    ) {
        fun push(key: JobContext.Key<*>) {
            ordered.add(key)
            if (!set.add(key)) {
                throw IllegalArgumentException(
                    "The following JobElementFactories have cyclic dependencies. The cyclic trace is as follows:\n${
                        ordered.joinToString(separator = " -> ") { it.name }
                    }"
                )
            }
        }

        fun pop() {
            set.remove(ordered.removeAt(ordered.size - 1))
        }
    }

    fun recursivelySort(factory: JobFacetFactory, trace: Trace) {
        trace.push(factory.key)
        if (!visited.add(factory.key)) return

        for (dependency in factory.dependencies) {
            recursivelySort(
                factoryMap[dependency]
                    ?: throw IllegalArgumentException(
                        "Factory: '${factory.key.name}' " +
                                "required other type: '${dependency.name}' to be installed in " +
                                "the current context and it was not found!"
                    ), trace
            )
        }

        trace.pop()
        stack.add(factory)
    }

    val trace = Trace(
        ArrayList(), HashSet()
    )
    for (factory in factories) {
        recursivelySort(
            factory, trace
        )
    }

    return stack
}