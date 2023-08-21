package com.durganmcbroom.jobs.progress.bars

import com.durganmcbroom.jobs.progress.Progress
import com.durganmcbroom.jobs.progress.ProgressNotifier
import com.durganmcbroom.jobs.progress.bars.CursorPosition.Companion.ZERO_ZERO
import com.durganmcbroom.jobs.progress.bars.CursorPosition.Companion.then
import java.io.OutputStream
import java.io.PrintStream
import java.util.logging.Logger

private const val BAR_WIDTH = 20

public data class CursorPosition private constructor(
    val relative: CursorPosition?,
    val rows: Int,
    val columns: Int
) {
    public fun toAbsolutePosition(): CursorPosition {
        val relativeAbsolute = relative?.toAbsolutePosition()
        return CursorPosition(
            null,
            rows + (relativeAbsolute?.rows ?: 0),
            columns + (relativeAbsolute?.columns ?: 0),
        )
    }

    public companion object {
        internal val ZERO_ZERO = CursorPosition(null, 0, 0)

        public fun CursorPosition.then(rows: Int, columns: Int): CursorPosition {
            return CursorPosition(this, rows, columns)
        }
    }
}

// You MUST surrender control to this output stream!! Immediately set the System.out (if thats what you are using) as the `out` provided upon instantiation.
public class TerminalHandler(
    private val realOut: OutputStream
) {
    public val out: OutputStream = object : OutputStream() {
        // We do not accept Ansi codes this way now
        override fun write(b: Int) {
            when (b.toChar()) {
                '\n' -> currentPosition =
                    currentPosition.then(-currentPosition.toAbsolutePosition().rows, 1)

                '\r' -> currentPosition =
                    currentPosition.then(-currentPosition.toAbsolutePosition().rows, 0)

                AnsiAccess.ESCAPE -> return // Disabling Ansi escape code support here
                else -> currentPosition = currentPosition.then(0, 1)
            }

            realOut.write(b)
        }
    }
    public var currentPosition: CursorPosition = ZERO_ZERO
        private set
    public val supportsAnsi: Boolean = AnsiAccess.supportsAnsi()

    // Moves the cursor relative to 0, 0
    public fun moveTo(relative: CursorPosition) {
        val absolute = relative.toAbsolutePosition()
        val currentAbsolute = currentPosition.toAbsolutePosition()

        fun write(str: String) {
            realOut.write(str.toByteArray())
        }

        if (absolute.columns > currentAbsolute.columns) {
            write(AnsiAccess.RIGHT_COLUMNS(absolute.columns - currentAbsolute.columns))
        } else {
            write(AnsiAccess.LEFT_COLUMNS(currentAbsolute.columns - absolute.columns))
        }

        if (absolute.rows > currentAbsolute.rows) {
            for (i in 0 until (absolute.rows - currentAbsolute.rows)) {
                write(AnsiAccess.DOWN_LINE)
            }
        } else {
            for (i in 0 until (currentAbsolute.rows - absolute.rows)) {
                write(AnsiAccess.UP_LINE_SCROLLING)
            }
        }
        realOut.flush()

        currentPosition =
            currentPosition.then(absolute.rows - currentAbsolute.rows, absolute.columns - currentAbsolute.columns)
    }

}

public fun main() {
    val handler = TerminalHandler(System.out)


    val (bars, out) = ProgressBars.new(handler)

    val newNotifier = bars.newNotifier()
    out.println("Hey how are you?")

    newNotifier.notify(Progress.from(0.5f), "Hey")
    out.println("Hey how are you?")

}

public class ProgressBars(
    private val terminal: TerminalHandler
) {
    private val notifiers: MutableList<BarProgressNotifier> = ArrayList()
    private var normalOut: CursorPosition = terminal.currentPosition
    private var progressOut: CursorPosition = terminal.currentPosition.then(0, 1)

    private fun moveProgress(by: Int) {
        progressOut = progressOut.then(0, by)
        notifiers.forEach { it.rewrite(progressOut) }
    }

    public fun newNotifier(): ProgressNotifier {
        return BarProgressNotifier(terminal, progressOut.then(notifiers.size, 0))
    }

    public companion object {
        public fun new(terminal: TerminalHandler): Pair<ProgressBars, PrintStream> {
            val bars = ProgressBars(terminal)

            val outStream = object : OutputStream() {
                private var location: CursorPosition = bars.normalOut

                override fun write(b: Int) {
                    terminal.out.write(b)
                    terminal.moveTo(location)

                    Logger.getAnonymousLogger().parent
                    if (b.toChar() == '\n') {
                        location = location.then(1, 0)
                        bars.moveProgress(1)
                    } else {
                        location = location.then(0, 1)
                    }
                }
            }

            return bars to PrintStream(outStream)
        }
    }
}

//↳
//
private class BarProgressNotifier(
    private val terminal: TerminalHandler,
    private var position: CursorPosition,
    private val topLevel: Boolean = true
) : ProgressNotifier {
    private var lastProgress = Progress.from(0f)
    private var lastExtra: String? = null

    public fun rewrite(updatedPos: CursorPosition) {
        val absolute = updatedPos.toAbsolutePosition()
        val currentAbsolute = position.toAbsolutePosition()

        position = position.then(absolute.rows - currentAbsolute.rows, absolute.columns - currentAbsolute.columns)

        notify(lastProgress, lastExtra)
    }

    override fun notify(update: Progress, extra: String?) {
        lastProgress = update
        lastExtra = extra

        val indent = if (!topLevel) "↳" else ""
//        val availableWidth = terminal.terminalSize.columns - metadata.indent - 4
        val progress = (0 until (BAR_WIDTH * update.progress).toInt())
            .joinToString(separator = "") { "=" } +
                (0 until (BAR_WIDTH - (BAR_WIDTH * update.progress).toInt()))
                    .joinToString(separator = "") { " " }
        val str = "$indent <$progress>"

        terminal.moveTo(position)
        terminal.out.write(str.toByteArray())

//        terminal.cursorPosition = metadata.position
//        terminal.putString(str)
    }

    override fun compose(): ProgressNotifier {
        TODO("Not yet implemented")
    }

}