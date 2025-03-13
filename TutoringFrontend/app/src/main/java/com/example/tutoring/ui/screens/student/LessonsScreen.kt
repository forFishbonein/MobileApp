package com.example.tutoring.ui.screens.student
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoring.data.Lesson
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.screens.student.common.LessonCard
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

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
    fun getAllLessons(){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                // Find the corresponding course content according to the courseId
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
            LessonCard(
                lesson = lesson
            )
        }
    }
}
