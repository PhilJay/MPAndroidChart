package com.xxmassdeveloper.mpchartexample

import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xxmassdeveloper.mpchartexample.fragments.SimpleChartDemo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewPagerTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<SimpleChartDemo>()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun smokeTestSimplyStart() {
        onView(ViewMatchers.isRoot())
                .captureToBitmap()
                .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")

        repeat(4) {
            onView(withId(R.id.pager)).perform(swipeLeft())
            onView(ViewMatchers.isRoot())
                    .captureToBitmap()
                    .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-${it}")
        }
    }
}
