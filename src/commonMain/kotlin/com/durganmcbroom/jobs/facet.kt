package com.durganmcbroom.jobs

@JobDsl
public fun <T : JobContext.Facet> JobScope.facet(key: JobContext.Key<T>): T {
    return checkNotNull(facetOrNull(key)) { "Failed to find facet: '${key.name}' in the current job context!" }
}

@JobDsl
public fun <T : JobContext.Facet> JobScope.facetOrNull(key: JobContext.Key<T>): T? {
    return context[key]
}
