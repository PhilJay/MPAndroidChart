package com.xxmassdeveloper.mpchartexample

import android.util.Log
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xxmassdeveloper.mpchartexample.notimportant.MainActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent

@RunWith(AndroidJUnit4::class)
class StartTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    var nameRule = TestName()

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun smokeTestStart() {
        Espresso.onView(ViewMatchers.isRoot())
            .captureToBitmap()
            .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}")

        MainActivity.menuItems.forEachIndexed { index, contentItem ->
            contentItem.clazz?.let {
                Log.d(nameRule.methodName, "Intended ${index}-${it.simpleName}")

                onData(anything())
                    .inAdapterView(allOf(withId(R.id.listView1), isCompletelyDisplayed()))
                    .atPosition(index).perform(click())

                Espresso.onView(ViewMatchers.isRoot())
                    .captureToBitmap()
                    .writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-${index}-${it.simpleName}")
                Intents.intended(hasComponent(it.name))
                Espresso.pressBack()
            }
        }
    }
}
