package com.github.mikephil.charting.compose

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.test.platform.app.InstrumentationRegistry
import java.lang.ref.WeakReference

/** Activity that shows [content] again every time it is created, so a recreate test gets the same composition back. */
class ChartHostActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { content() }
    }

    companion object {
        var content: @Composable () -> Unit = {}
    }
}

/**
 * Runs the garbage collector until every reference in [refs] is cleared or [timeoutMs] passes, and returns how
 * many are still reachable.
 */
@Suppress("DEPRECATION")
fun awaitCollected(refs: List<WeakReference<*>>, timeoutMs: Long = 10_000): Int {
    val deadline = SystemClock.uptimeMillis() + timeoutMs
    while (true) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Runtime.getRuntime().gc()
        System.runFinalization()
        Runtime.getRuntime().gc()
        val alive = refs.count { it.get() != null }
        if (alive == 0 || SystemClock.uptimeMillis() > deadline) return alive
        Thread.sleep(100)
    }
}
