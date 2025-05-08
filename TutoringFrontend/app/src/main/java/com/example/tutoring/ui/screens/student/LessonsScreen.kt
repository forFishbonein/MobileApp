package com.example.tutoring.ui.screens.student
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoring.data.Lesson
import com.example.tutoring.data.LessonsProcess
import com.example.tutoring.data.UserInfo
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.screens.student.common.LessonCard
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import com.google.accompanist.pager.*
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LessonsScreen(courseId: Int?, loadingViewModel: LoadingViewModel = viewModel()) {
    if (loadingViewModel.isHttpLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    var lessons by remember { mutableStateOf(listOf<Lesson>()) }
    // PagerState controls the number of pages currently displayed
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    val context = LocalContext.current
    fun getAllLessons(userId:Int){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                // Find the corresponding course content according to the courseId
                val response = apiService.listLessons(courseId)
//                lessons = response.data as List<Lesson>
                val response2 = apiService.getLessonProgressByCourseAndStudent(courseId, userId)
                // Get the progress list
                val progressList = response2.data as List<LessonsProcess>
                // Match each lesson in the lessons to the progressList and update the completed field
                lessons = (response.data as List<Lesson>).map { lesson ->
//                    // Check whether there is a corresponding progress record and certain conditions are met (for example, status == "completed")
//                    val matchingProgress = progressList.find { progress ->
//                        progress.lessonId == lesson.lessonId && progress.status == "completed"
//                    }
//                    // If a record is found, the lesson is complete, otherwise it is not
//                    lesson.copy(completed = matchingProgress != null)
                    // Find the matching progress
                    val matchingProgress = progressList.find { progress ->
                        progress.lessonId == lesson.lessonId && progress.status == "completed"
                    }
                    // First, turn the fields that might be deserialized as null into empty strings
                    val safeImageUrls = (lesson.imageUrls as String?) ?: ""
                    val safePdfUrls   = (lesson.pdfUrls   as String?) ?: ""
                    // Assign values to all fields at once again via copy
                    lesson.copy(
                        completed = matchingProgress != null,
                        imageUrls = safeImageUrls,
                        pdfUrls   = safePdfUrls
                    )
                }
                Log.d(
                    "LessonsScreen",
                    "lessons: ${lessons}, progressList: $progressList"
                )
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                Log.e("LessonsScreen", "error happend：", e)
                loadingViewModel.setLoading(false)
            }
        }

    }
    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userInfoJson = sharedPrefs.getString("user_info", null)
        if (!userInfoJson.isNullOrBlank()) {
            val gson = Gson()
            val userInfo = gson.fromJson(userInfoJson, UserInfo::class.java)
//            Log.d(
//                "userInfo",
//                "userInfo: ${userInfo}, userId: ${userInfo.userId}, type: ${userInfo.userId::class.java.simpleName}"
//            )
            getAllLessons(userInfo.userId)
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Completion of lessons: ${lessons.count { it.completed }} / ${lessons.size}")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (pagerState.currentPage > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Text("Previous Lesson")
            }

            Button(
                onClick = {
                    // Click to jump to the next lesson, allowed only if the status of the current lesson is "completed"
                    if (pagerState.currentPage < lessons.size - 1 &&
                        lessons[pagerState.currentPage].completed) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                enabled = pagerState.currentPage < lessons.size - 1 &&
                        lessons[pagerState.currentPage].completed
            ) {
                Text("Next Lesson")
            }
        }
        // Create a horizontal Pager
        HorizontalPager(
            state = pagerState,
            count = lessons.size,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false // No gesture swiping, you must click the button
        ) { pageIndex ->
            val lesson = lessons[pageIndex]
            Log.d("LessonsScreen", "当前 lesson = $lesson")
            LessonCard(
                lesson = lesson,
                onChangeComplete = {
                    coroutineScope.launch {
                        try {
                            val response = lesson.lessonId?.let {
                                if (courseId != null) {
                                    apiService.completeLessonForSelf(it,courseId)
                                }
                            }
                            ErrorNotifier.showSuccess("Mark Successful!")
                            val updated = lessons.toMutableList()
                            updated[pageIndex] = updated[pageIndex].copy(completed = true)
                            lessons = updated
                        } catch (e: Exception) {
                            ErrorNotifier.showError(e.message ?: "Failed.")
                        }
                    }
                },
            )
        }
    }
}
