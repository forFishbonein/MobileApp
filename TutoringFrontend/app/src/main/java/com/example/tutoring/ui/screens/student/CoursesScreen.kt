package com.example.tutoring.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tutoring.data.Course
import com.example.tutoring.data.Registration
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.screens.student.common.CourseCard
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavHostController, loadingViewModel: LoadingViewModel = viewModel()) {
    // Global load indicator (overlay)
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
    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var courses by remember { mutableStateOf(listOf<Course>()) }
    var allCourses by remember { mutableStateOf(listOf<Registration>()) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    val pageSize = 10
    fun getAllCourses(){
        scope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.listStudentRegistrations()

                val registrationsList = response.data as List<Registration>
                // It filtered out and was successfully ordered
                allCourses = registrationsList.filter { it.status == "approved" }

                @Suppress("UNCHECKED_CAST")
                courses = allCourses.take(pageSize).map { registration ->
                    val detailResponse = apiService.getCourseDetail(registration.courseId)
                    val detail = detailResponse.data as Course
                    // Merge data: Duplicate fields use the value returned by detail, leaving status in registration
                    Course(
                        courseId = registration.courseId,
                        courseName = detail.courseName,
                        description = detail.description,
                        subject = detail.subject,
                        status = registration.status,
                        updatedAt = detail.updatedAt,
                        createdAt = detail.createdAt
                    )
                } ?: emptyList()
                page++
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
            }
        }
    }
    fun loadCourses() {
        if (isLoading) return
        isLoading = true

        scope.launch {
            //            delay(1000)
            // Calculate starting and ending subscripts based on page numbers
            val startIndex = (page - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, allCourses.size)
            // Intercepts the data of the current page and returns an empty list if it is out of range
            val newCourses = if (startIndex < allCourses.size) {
                allCourses.subList(startIndex, endIndex).map { registration ->
                    val detailResponse = apiService.getCourseDetail(registration.courseId)
                    val detail = detailResponse.data as Course
                    Course(
                        courseId = registration.courseId,
                        courseName = detail.courseName,
                        description = detail.description,
                        subject = detail.subject,
                        status = registration.status,
                        updatedAt = detail.updatedAt,
                        createdAt = detail.createdAt
                    )
                }
            } else {
                emptyList()
            }
            // Appends to the original list
            courses = courses + newCourses
            page++
            isLoading = false
//            // Generate fake data
//            val newCourses = (1..10).map {
//                val index = ((page - 1) * 10) + it
//                Course(
//                    id = index,
//                    courseName = "Course #$index",
//                    subjectName = "Subject #${index % 3 + 1}",
//                    status = ""
//                )
//            }

        }
    }

    // Load data the first time you enter the page
    LaunchedEffect(Unit) {
        getAllCourses()
    }

    // Bottom load: When the list rolls to the end, the next page is loaded
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && lastVisibleItemIndex == courses.lastIndex) {
                    loadCourses()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 课程列表
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(courses.size) { index ->
                val course = courses[index]
                CourseCard(
                    cardType = "courses",
                    course = course,
                    onJoinClick = {},
                    navController = navController
                )
            }

            // Bottom loading prompts
            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

    }
}
