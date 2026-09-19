package com.xxmassdeveloper.mpchartexample.custom

/**
 * Collects how long the draws of one chart took. Recorded and read on the thread that draws, so the
 * readout never has to wait for the chart and the chart never has to wait for the readout.
 */
class FrameTimes {

    private val drawnAt = LongArray(CAPACITY)
    private val tookNanos = LongArray(CAPACITY)
    private var recorded = 0L

    fun record(durationNanos: Long) {
        val slot = (recorded % CAPACITY).toInt()
        drawnAt[slot] = System.nanoTime()
        tookNanos[slot] = durationNanos
        recorded++
    }

    fun clear() {
        recorded = 0
    }

    /** Mean draw time in milliseconds over the last [HEADLINE_FRAMES] frames, 0 before the first frame. */
    val recentMillis: Float
        get() {
            val frames = minOf(recorded, HEADLINE_FRAMES.toLong()).toInt()
            if (frames == 0) return 0f

            var sum = 0L
            for (i in 1..frames) sum += tookNanos[((recorded - i) % CAPACITY).toInt()]
            return sum / frames / NANOS_PER_MILLI
        }

    /** Longest draw in milliseconds within the last second, 0 when nothing was drawn in it. */
    val worstMillisLastSecond: Float
        get() {
            val since = System.nanoTime() - ONE_SECOND_NANOS
            var worst = 0L
            for (i in 1..minOf(recorded, CAPACITY.toLong()).toInt()) {
                val slot = ((recorded - i) % CAPACITY).toInt()
                if (drawnAt[slot] < since) break
                if (tookNanos[slot] > worst) worst = tookNanos[slot]
            }
            return worst / NANOS_PER_MILLI
        }

    private companion object {
        const val CAPACITY = 256
        const val HEADLINE_FRAMES = 30
        const val ONE_SECOND_NANOS = 1_000_000_000L
        const val NANOS_PER_MILLI = 1_000_000f
    }
}
