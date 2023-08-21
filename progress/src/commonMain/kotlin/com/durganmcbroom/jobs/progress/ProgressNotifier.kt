package com.durganmcbroom.jobs.progress

import com.durganmcbroom.jobs.Composable
import com.durganmcbroom.jobs.CompositionStub

public interface ProgressNotifier<T: CompositionStub> : Composable<ProgressNotifier<T>, T> {
    public fun notify(update: Progress, extra: String?)
}