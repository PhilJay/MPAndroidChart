package com.github.mikephil.charting.benchmark

import android.os.Debug
import android.util.Log

/**
 * Times a block after warmup runs and reports the median and best sample together with the number of
 * garbage collections the measured runs triggered. Results go to logcat under [TAG] as tab separated rows.
 */
object Bench {

    const val TAG = "MPBENCH"

    private fun gcCount(): Long = Debug.getRuntimeStat("art.gc.gc-count")?.toLongOrNull() ?: -1L

    /**
     * @param unitsPerRun how many units of work [block] performs, so a block that draws 30 frames reports
     * the cost of one frame.
     */
    fun run(
        scenario: String,
        variant: String,
        points: Int,
        warmups: Int = 4,
        iterations: Int = 7,
        unitsPerRun: Int = 1,
        setup: () -> Unit = {},
        block: () -> Unit,
    ) {
        try {
            repeat(warmups) {
                setup()
                block()
            }

            val samples = DoubleArray(iterations)
            val gcBefore = gcCount()
            for (i in 0 until iterations) {
                setup()
                val start = System.nanoTime()
                block()
                samples[i] = (System.nanoTime() - start) / 1_000_000.0 / unitsPerRun
            }
            val gcPerRun = (gcCount() - gcBefore).toDouble() / iterations

            samples.sort()
            emit(scenario, variant, points, samples[iterations / 2], samples[0], gcPerRun)
        } catch (t: Throwable) {
            Log.e(TAG, "FAIL\t$scenario\t$variant\t$points\t${t::class.java.simpleName}: ${t.message}")
        }
    }

    private fun emit(scenario: String, variant: String, points: Int, median: Double, best: Double, gc: Double) {
        Log.i(TAG, "ROW\t$scenario\t$variant\t$points\t${fmt(median)}\t${fmt(best)}\t${fmt(gc)}")
    }

    private fun fmt(value: Double): String = String.format("%.3f", value)
}
