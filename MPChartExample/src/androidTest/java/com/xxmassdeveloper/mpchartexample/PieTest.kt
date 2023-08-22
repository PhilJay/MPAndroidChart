package com.xxmassdeveloper.mpchartexample

import android.view.View
import android.widget.SeekBar
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PieTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<PiePolylineChartActivity>()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun piePolyline() {
        onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")

        for (x in 10..50 step 10) {
            onView(withId(R.id.seekBarX)).perform(setProgress(x))
            onView(ViewMatchers.isRoot())
                .captureToBitmap()
                .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}1-${x}")
        }

        for (x in 50..200 step 50) {
            onView(withId(R.id.seekBarY)).perform(setProgress(x))
            onView(ViewMatchers.isRoot())
                .captureToBitmap()
                .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}2-${x}")
        }
    }

    private fun setProgress(progress: Int): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController?, view: View) {
                val seekBar = view as SeekBar
                seekBar.progress = progress
            }

            override fun getDescription(): String {
                return "Set a progress on a SeekBar"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(SeekBar::class.java)
            }
        }
    }

}
