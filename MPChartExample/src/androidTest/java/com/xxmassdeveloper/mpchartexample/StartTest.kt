package com.xxmassdeveloper.mpchartexample

import android.graphics.Bitmap
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.captureToBitmap
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase.Companion.optionMenus
import com.xxmassdeveloper.mpchartexample.notimportant.MainActivity
import info.hannes.timber.DebugFormatTree
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import timber.log.Timber


@RunWith(AndroidJUnit4::class)
class StartTest {

    @get:Rule
    val activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    var nameRule = TestName()

    @Before
    fun setUp() {
        Intents.init()
        Timber.plant(DebugFormatTree())
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun smokeTestStart() {
        onView(ViewMatchers.isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}") })

        var optionMenu = ""
        // iterate samples
        MainActivity.menuItems.forEachIndexed { index, contentItem ->
            contentItem.clazz?.let {
                Timber.d("Intended ${index}-${it.simpleName}")

                try {
                    onData(anything())
                        .inAdapterView(allOf(withId(R.id.listViewMain), isCompletelyDisplayed()))
                        .atPosition(index).perform(click())

                    Intents.intended(hasComponent(it.name))
                    onView(ViewMatchers.isRoot())
                        .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-${index}-${it.simpleName}-${contentItem.name}-1SampleClick") })

                    optionMenu = ""
                    optionMenus.filter { plain -> Character.isDigit(plain.first()) }.forEach { filteredTitle ->
                        optionMenu = "$index->$filteredTitle"
                        openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
                        screenshotOfOptionMenu("${javaClass.simpleName}_${nameRule.methodName}-${index}-${it.simpleName}-${contentItem.name}", filteredTitle)
                    }

                    // Espresso.pressBack()
                    //Thread.sleep(100)
                    Espresso.pressBack()
                } catch (e: Exception) {
                    Timber.e(optionMenu + e.message!!)
                    onView(ViewMatchers.isRoot())
                        .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${javaClass.simpleName}_${nameRule.methodName}-${index}-${it.simpleName}-Error") })
                }
            }
        }
    }

    private fun screenshotOfOptionMenu(simpleName: String, menuTitle: String) {
        onView(withText(menuTitle)).perform(click())
        Timber.d("screenshotOfOptionMenu ${menuTitle}-${simpleName}")
        onView(ViewMatchers.isRoot())
            .perform(captureToBitmap { bitmap: Bitmap -> bitmap.writeToTestStorage("${simpleName}-2menu-click-$menuTitle") })
    }

}
