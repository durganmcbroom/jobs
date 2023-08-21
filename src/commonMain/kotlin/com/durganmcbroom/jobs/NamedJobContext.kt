package com.durganmcbroom.jobs

public interface NamedJobContext<T: NamedCompositionStub> : JobContext<T> {
    public val name: String
}

public interface NamedCompositionStub : CompositionStub {
    public val name: String
}