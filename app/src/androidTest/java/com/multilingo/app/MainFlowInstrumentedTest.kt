package com.multilingo.app

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainFlowInstrumentedTest {
    private lateinit var device: UiDevice
    private val packageName = "com.multilingo.app"

    @Before
    fun launchApp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        assertNotNull("Launcher activity must exist", launchIntent)
        launchIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(launchIntent)
        device.waitForIdle()
    }

    @Test
    fun welcomeHomeCourseLessonAndProfileNavigationWork() {
        val start = device.wait(Until.findObject(By.text("Start learning")), 10_000)
        assertNotNull("Welcome screen should show Start learning", start)
        start.click()

        val learnTab = device.wait(Until.findObject(By.text("Learn")), 5_000)
        assertNotNull("Home screen should show Learn navigation", learnTab)
        learnTab.click()

        val javaCourse = device.wait(Until.findObject(By.text("Java")), 5_000)
        assertNotNull("Course list should contain Java", javaCourse)
        javaCourse.click()

        val lessonTitle = device.wait(Until.findObject(By.text("Print a message")), 5_000)
        assertNotNull("Selecting Java should open its first lesson", lessonTitle)

        val correctChoice = device.wait(Until.findObject(By.text("System")), 5_000)
        assertNotNull("Java lesson should show answer choices", correctChoice)
        correctChoice.click()

        val check = device.wait(Until.findObject(By.text("Check answer")), 5_000)
        assertNotNull("Lesson should allow checking an answer", check)
        check.click()

        val correctFeedback = device.wait(Until.findObject(By.textContains("Correct!")), 5_000)
        assertNotNull("Correct answer should show feedback", correctFeedback)

        val exit = device.wait(Until.findObject(By.desc("Exit lesson")), 5_000)
        assertNotNull("Lesson should provide an exit action", exit)
        exit.click()

        val profile = device.wait(Until.findObject(By.text("Profile")), 5_000)
        assertNotNull("Home should provide Profile navigation", profile)
        profile.click()

        val progress = device.wait(Until.findObject(By.text("Your progress")), 5_000)
        assertNotNull("Profile screen should render", progress)
        assertTrue("App package should remain installed", device.isPackageInstalled(packageName))
    }
}
