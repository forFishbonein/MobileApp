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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
fun HomeScreen(loadingViewModel: LoadingViewModel = viewModel()) {
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
    var courseName by remember { mutableStateOf("") }
    var subjectName by remember { mutableStateOf("") }

    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var courses by remember { mutableStateOf(listOf<Course>()) }
    var allCourses by remember { mutableStateOf(listOf<Course>()) }
    var allRegistrations by remember { mutableStateOf(listOf<Registration>()) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    val pageSize = 5
    fun getAllCourses(){
        loadingViewModel.setLoading(true)
        scope.launch {
            try {
                val response = apiService.listCourses(courseName, subjectName)
                allCourses = (response.data as List<Course>)
                val response2 = apiService.listStudentRegistrations()
                allRegistrations = response2.data as List<Registration>
                @Suppress("UNCHECKED_CAST")
                courses = allCourses.take(pageSize).map { course ->
                    val matchingRegistration = allRegistrations.find { it.courseId == course.courseId }
                    course.copy(status = matchingRegistration?.status ?: "")
                } ?: emptyList()
                page++
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
            }
        }
    }
    fun loadCourses() {
//        if (isLoading) return
        // If it is loading or there are no more pages left, return directly
        val startIndex = (page - 1) * pageSize
        if (isLoading || startIndex >= allCourses.size) return
        isLoading = true
        scope.launch {
            delay(1000)
            val startIndex = (page - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, allCourses.size)
            val newCourses = if (startIndex < allCourses.size) {
                allCourses.subList(startIndex, endIndex).map { course ->
                    val matchingRegistration = allRegistrations.find { it.courseId == course.courseId }
                    course.copy(status = matchingRegistration?.status ?: "")
                }
            } else {
                emptyList()
            }
            courses = courses + newCourses
            page++
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        getAllCourses()
    }

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = { Text("Course Name") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                label = { Text("Subject Name") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                // When you click Search, you reset the page number and reload the data
                page = 1
                courses = emptyList()
                getAllCourses()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Courses List
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(courses.size) { index ->
                val course = courses[index]
                CourseCard(
                    course = course,
                    onJoinClick = {
                        scope.launch {
                            try {
                                val requestBody = mapOf(
                                    "courseId" to course.courseId,
                                )
                                val response = apiService.registerCourse(requestBody)
                                ErrorNotifier.showSuccess("Apply Successful! Please wait for the tutor to confirm.")
                                // Change the status of the corresponding course to "Pending"
                                val updated = courses.toMutableList()
                                updated[index] = updated[index].copy(status = "pending")
                                courses = updated
                            } catch (e: Exception) {
                                ErrorNotifier.showError(e.message ?: "Join Failed.")
                            }
                        }
                    }
                )
            }

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
