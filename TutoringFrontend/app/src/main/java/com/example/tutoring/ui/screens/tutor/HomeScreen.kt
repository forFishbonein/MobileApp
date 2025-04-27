package com.example.tutoring.ui.screens.tutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import com.example.tutoring.data.StudentProgress
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoring.data.CourseProgress
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import kotlinx.coroutines.launch

//import com.example.tutoring.data.mockCourses

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//fun HomeScreen(courses: List<CourseStats> = mockCourses) {
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
    // 1) 原始从接口拿到的注册列表
    var registrations by remember { mutableStateOf<List<CourseRegistration>>(emptyList()) }
    // 1.1) 去重后的课程列表
//    val courses = registrations
//        .distinctBy { it.courseId }
//        .map { CourseRegistration(it.courseId, it.courseName) }
    // 2) 下拉展开状态
    var expanded by remember { mutableStateOf(false) }
    // 3) 选中的课程
    var selectedCourse by remember { mutableStateOf<CourseRegistration?>(null) }
    // 4) 仪表盘数据：某个课程的进度列表
    var progressList by remember { mutableStateOf<CourseProgress?>(null) }
    val apiService = NetworkClient.createService(ApiService::class.java)
    val scope = rememberCoroutineScope()
    // Helper：根据 courseId 拉仪表盘数据
    fun loadDashboard(courseId: Int) {
        scope.launch {
            loadingViewModel.setLoading(true)
            try {
                val resp = apiService.getTutorDashboardInfo(courseId)
                progressList = resp.data
            } catch (e: Exception) {
                ErrorNotifier.showError(e.message ?: "Network error")
            } finally {
                loadingViewModel.setLoading(false)
            }
        }
    }
//    fun getDashboardInfo() {
//        loadingViewModel.setLoading(true)
//        scope.launch {
//            try {
//                val resp = apiService.getTutorDashboardInfo()
//                dashboard = resp.data ?: emptyList()
//                if (dashboard.isNotEmpty() && selectedCourse == null) {
//                    selectedCourse = dashboard.first()
//                }
//                loadingViewModel.setLoading(false)
//            } catch (e: Exception) {
//                ErrorNotifier.showError(e.message ?: "Network error")
//                loadingViewModel.setLoading(false)
//            }
//        }
//    }
    // 启动时先拉课程列表，然后默认选第一个、并拉仪表盘
    LaunchedEffect(Unit) {
        loadingViewModel.setLoading(true)
        try {
            val resp = apiService.listTutorCourses()
            // 拿到后端返回的 CourseRegistration 列表
            registrations = resp.data ?: emptyList()
            // 默认取第一个
            registrations.firstOrNull()?.let { firstCourse ->
                selectedCourse = firstCourse
                loadDashboard(firstCourse.courseId)
            }
        } catch (e: Exception) {
            ErrorNotifier.showError(e.message ?: "Network error")
        } finally {
            loadingViewModel.setLoading(false)
        }
    }
    // “鼠标悬停” 在手机上不太通用，改成点击 Info 弹框
    var showInfo by remember { mutableStateOf(false) }
    val fullDesc = "Each student’s completion status for the lessons in the courses taught."
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Course completion status",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
            )
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = { showInfo = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "More Information"
                )
            }
        }

        if (showInfo) {
            AlertDialog(
                onDismissRequest = { showInfo = false },
                title   = { Text("Description") },
                text    = { Text(fullDesc) },
                confirmButton = {
                    TextButton(onClick = { showInfo = false }) {
                        Text("I Know")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = selectedCourse?.courseName ?: "Choose a course",
                onValueChange = { },
                readOnly = true,
                label = { Text("Course") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                registrations.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course.courseName) },
                        onClick = {
                            // 切换课程，拉对应的数据
                            selectedCourse = course
                            expanded = false
                            loadDashboard(course.courseId)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // —— 已选课程的仪表盘 ——
        selectedCourse?.let { course ->
            Text(
                "Enrolled Students: ${progressList?.students?.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            // 进度图表
            progressList?.students?.let { StudentProgressChart(progressList = it) }
        }
    }
}

@Composable
fun StudentProgressChart(progressList: List<StudentProgress>) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        progressList.forEach { sp ->
            // 假设 sp.progressPercent 是 0..100
            val frac = sp.progressPercent / 100f
            val percent = sp.progressPercent

            val barColor = when {
                percent >= 100 -> Color(0xFF4CAF50) // Green
                percent >= 50 -> Color(0xFFFFA000) // Orange
                else -> Color(0xFFD32F2F)          // Red
            }

            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(text = sp.nickname)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(frac)
                            .background(barColor, shape = MaterialTheme.shapes.medium)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("$percent% Complete", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DashboardScreenPreview() {
//    TutorDashboardScreen()
//}
