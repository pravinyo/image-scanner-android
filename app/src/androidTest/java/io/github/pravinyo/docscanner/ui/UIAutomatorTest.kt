package io.github.pravinyo.docscanner.ui

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import io.github.pravinyo.docscanner.ApplicationController
import io.github.pravinyo.docscanner.R
import io.github.pravinyo.docscanner.dependencyInjection.DaggerAndroidTestComponent
import io.github.pravinyo.docscanner.dependencyInjection.app.AppModule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UIAutomatorTest {

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
    fun validateAppStateIsPreservedWhenHomePressed() {
        onView(withId(R.id.editButton))
            .perform(click())

        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.pressHome()

        val context: Context = InstrumentationRegistry.getInstrumentation().context
        val i = Intent()
        i.setClassName("io.github.pravinyo.docscanner.free",
            "io.github.pravinyo.docscanner.ui.MainActivity")
        i.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        context.startActivity(i)
    }
}