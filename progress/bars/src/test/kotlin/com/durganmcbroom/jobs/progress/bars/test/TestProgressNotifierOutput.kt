package com.durganmcbroom.jobs.progress.bars.test

import com.durganmcbroom.jobs.progress.Progress
import com.durganmcbroom.jobs.progress.bars.BarProgressNotifier
import kotlin.test.Test

class TestProgressNotifierOutput {
    @Test
    fun `Test correct output`() {
        System.setProperty("java.awt.headless","true")

        val notifier = BarProgressNotifier.new(
        )
        Thread.sleep(3000)

        notifier.notify(Progress.from(0f), null)
        Thread.sleep(500)
        notifier.notify(Progress.from(0.25f), null)
        Thread.sleep(500)
        notifier.notify(Progress.from(0.5f), null)
        Thread.sleep(500)
        notifier.notify(Progress.from(0.75f), null)
        Thread.sleep(500)
        notifier.notify(Progress.from(1f), null)

        Thread.sleep(10000)
    }
}