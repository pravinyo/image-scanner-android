package io.github.pravinyo.docscanner.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import io.github.pravinyo.docscanner.ApplicationController
import io.github.pravinyo.docscanner.R
import io.github.pravinyo.docscanner.dependencyInjection.DaggerAndroidTestComponent
import io.github.pravinyo.docscanner.dependencyInjection.app.AppModule
import org.junit.Before
import org.junit.Test

class PageFragmentTest {

    @Suppress("DEPRECATION")
    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<ApplicationController>()

        val testAppComponent = DaggerAndroidTestComponent.builder()
            .appModule(AppModule(app))
            .build()
        testAppComponent.inject(this)

        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun fragmentScreenVisible () {
        onView(withId(R.id.toolbar))
            .check(matches(isEnabled()))
    }

    @Test
    fun imageDocumentVisible () {
        onView(withId(R.id.displayImage))
                .check(matches(isDisplayed()))
    }

    @Test
    fun editImageScreenNavigation () {
        onView(withId(R.id.editButton))
                .perform(click())
        onView(withId(R.id.saveButton))
                .check(matches(isDisplayed()))
    }
}