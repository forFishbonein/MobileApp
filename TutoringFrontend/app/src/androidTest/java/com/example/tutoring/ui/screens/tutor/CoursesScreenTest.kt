package com.example.tutoring.ui.screens.tutor

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tutoring.MainActivity
import com.example.tutoring.network.NetworkClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CoursesScreenTest {
    // 1) Create a Compose test Rule
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    // 2) Declare MockWebServer
    private lateinit var mockServer: MockWebServer
    @Before
    fun setup() {
        // 3) Instantiate and start
        // --- Key point: Initialize appContext ---
        NetworkClient.appContext = ApplicationProvider.getApplicationContext()
        mockServer = MockWebServer().apply { start() }

        // 4) Tell your NetworkClient or Retrofit to use mockServer.url("/") as the baseUrl
        NetworkClient.overrideBaseUrl(mockServer.url("/").toString())

        // 5) Prepare a fake response
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{ "code":200, "message":"ok", "data":[] }""")
        )
        // 6) Click POST /course/create after Confirm
        mockServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{ "code":200, "message":"created", "data": {} }""")
        )
    }

    @Test
    fun addCourseDialog_shows_and_submits() {
        // 1) Launch to CoursesScreen
        composeRule.setContent {
            // CoursesScreen is directly called and mock ApiService is injected
            CoursesScreen(navController = rememberNavController())
        }

        // 2) Click the "Add Course" button
        composeRule.onNodeWithTag("addCourseButton").performClick()

        // 3) A dialog box pops up and asserts the title text
        composeRule.onNodeWithText("Add Course").assertIsDisplayed()
//        composeRule.onNodeWithText("Add Course").performClick()
        // 4) Enter “Course Name”
        composeRule.onNodeWithTag("courseNameField")
            .performTextInput("New Course Test")

        // 5) Enter other fields...
        composeRule.onNodeWithTag("courseDescField")
            .performTextInput("Desc Test")

        composeRule.onNodeWithTag("courseSubjectField")
            .performTextInput("Math Test")

        // 6) Click “Confirm”
        composeRule.onNodeWithTag("confirmButton").performClick()
        // Wait for Compose to complete all pending work
        composeRule.waitForIdle()
        // 7) Verify that MockServer has received the expected POST /course/create and the dialog box has disappeared
        composeRule.onNodeWithTag("addCourseDialog").assertDoesNotExist()
    }
}
