package info.appdev.chartexample

import android.graphics.Bitmap
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import info.appdev.chartexample.fragments.ViewPagerSimpleChartDemo
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewPagerTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<ViewPagerSimpleChartDemo>()

    @get:Rule
    var nameRule = TestName()

    @Test
    fun smokeTestViewPager() {
        Thread.sleep(SHORT_DURATION_MS)
        onView(ViewMatchers.isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}") })

        repeat(4) {
            onView(withId(R.id.pager)).perform(swipeLeft())
            Thread.sleep(SHORT_DURATION_MS)
            onView(ViewMatchers.isRoot())
                .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-${it}") })
        }
    }

    companion object {
        private const val SHORT_DURATION_MS = 1500L
    }
}
