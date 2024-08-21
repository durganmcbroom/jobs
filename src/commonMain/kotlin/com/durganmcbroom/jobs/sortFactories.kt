package com.durganmcbroom.jobs

internal fun sortFactories(
    thisContext: JobContext,
    context: JobContext = EmptyJobContext,
): List<JobFacetFactory> {
    val factories = context.factories
        .takeIf { it.isNotEmpty() }
        ?.let { topologicalSort(thisContext.factories + it) }
        ?: thisContext.factories

    return factories.reversed()
}