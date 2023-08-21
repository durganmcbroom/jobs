package com.durganmcbroom.jobs

public interface Composable<Self: Composable<Self, T>, T: CompositionStub> {
    public fun compose(stub: T) : Self
}