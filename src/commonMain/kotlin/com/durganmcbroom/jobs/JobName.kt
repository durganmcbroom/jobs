package com.durganmcbroom.jobs

public class JobName(
    public val name: String
) : JobContext.Facet {
    override val key: JobContext.Key<*> = JobName

    public companion object : JobContext.Key<JobName> {
        override val name: String = "Job Name"
    }
}

public val JobScope.jobName : String
    get() = context[JobName]?.name ?: "unnamed"