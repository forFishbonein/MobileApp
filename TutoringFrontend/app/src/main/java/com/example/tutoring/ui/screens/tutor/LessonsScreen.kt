package com.example.tutoring.ui.screens.tutor
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tutoring.data.Lesson
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.navigation.tutor.TutorNavRoutes
import com.example.tutoring.ui.screens.tutor.common.LessonCardTutor
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun LessonsScreen(courseId: Int?, navController: NavHostController,loadingViewModel: LoadingViewModel = viewModel()) {
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
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    fun getAllLessons(){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.listLessons(courseId)
                lessons = response.data as List<Lesson>
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
            }
        }
    }
    LaunchedEffect(Unit) {
        getAllLessons()
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
                    if (pagerState.currentPage < lessons.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                enabled = pagerState.currentPage < lessons.size - 1
            ) {
                Text("Next Lesson")
            }
        }
        HorizontalPager(
            state = pagerState,
            count = lessons.size,
            modifier = Modifier
                .fillMaxWidth(),
//                    .height(575.dp),   // FIX HEIGHT
//                    .padding(horizontal = 16.dp),
            userScrollEnabled = true // gesture swiping is ok
        ) { pageIndex ->
            val lesson = lessons[pageIndex]
            LessonCardTutor(
                lesson = lesson,
                onChangeComplete = {
                    coroutineScope.launch {
                        try {
                            val response = lesson.lessonId?.let { apiService.completeLesson(it) }
                            ErrorNotifier.showSuccess("Mark Successful!")
                            val updated = lessons.toMutableList()
                            updated[pageIndex] = updated[pageIndex].copy(completed = true)
                            lessons = updated
                        } catch (e: Exception) {
                            ErrorNotifier.showError(e.message ?: "Failed.")
                        }
                    }
                },
                courseId = courseId,
                navController = navController
            )
        }
        Box(modifier = Modifier.fillMaxWidth()){
            // Add the "Add New Course" button in the bottom right corner of the Box
            Button(
                onClick = {
                    navController.navigate("tutor_add_lesson?courseId=${courseId}&lesson=")
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("Add New Lesson")
            }
        }
    }
}
