package com.durganmcbroom.jobs

public fun interface Job<in T: JobContext<*>, out O: JobOutput<*, *>> {
    public fun run(context: T) : O
}