package com.durganmcbroom.jobs.progress.simple

import com.durganmcbroom.jobs.CompositionStub
import com.durganmcbroom.jobs.logging.LoggingContext
import com.durganmcbroom.jobs.progress.ProgressingJobContext

public interface SimpleProgressJobContext<T: CompositionStub> : ProgressingJobContext<T, SimpleNotifierStub>, LoggingContext<T>