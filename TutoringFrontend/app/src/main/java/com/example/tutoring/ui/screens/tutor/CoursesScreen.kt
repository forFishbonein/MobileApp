package com.example.tutoring.ui.screens.tutor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.screens.tutor.common.CourseCardTutor
import com.example.tutoring.utils.ErrorNotifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavHostController) {

    // 模拟分页数据
    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var courses by remember { mutableStateOf(listOf<CourseRegistration>()) }
    var allCourses by remember { mutableStateOf(listOf<CourseRegistration>()) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    val pageSize = 10
    fun getAllCourses(){
        scope.launch {
            try {
                val response = apiService.listTutorCourses()
                allCourses = (response.data as List<CourseRegistration>).map { course ->
                    course.copy(registrationId = 0) // 例如默认值设为 0
                }
                courses = allCourses.take(pageSize)
                page++
            } catch (e: Exception) {
                ErrorNotifier.showError(e.message ?: "Register failed.")
            }
        }
    }
    // 模拟加载数据函数
    fun loadCourses() {
        // 如果正在加载，直接返回
        if (isLoading) return
        isLoading = true
        // 模拟网络请求
        scope.launch {
            delay(1000) // 模拟网络延迟
// 根据页码计算起始和结束下标
            val startIndex = (page - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, allCourses.size)
// 截取当前页的数据，如果超出范围则返回空列表
            val newCourses = if (startIndex < allCourses.size) {
                allCourses.subList(startIndex, endIndex)
            } else {
                emptyList()
            }
            // 在原有列表后追加
            courses = courses + newCourses
            page++
            isLoading = false
        }
    }

    // 首次进入页面时加载数据
    LaunchedEffect(Unit) {
        getAllCourses()
    }

    // 触底加载：当列表滚动到末尾时，加载下一页
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && lastVisibleItemIndex == courses.lastIndex) {
                    loadCourses()
                }
            }
    }
    // 用于控制弹窗显示
    var showAddDialog by remember { mutableStateOf(false) }
    // 表单字段
    var courseName by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var courseSubject by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 添加课程按钮
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Course")
        }

        // 弹出对话框：添加课程表单
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Course") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = courseName,
                            onValueChange = { courseName = it },
                            label = { Text("Course Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = courseDescription,
                            onValueChange = { courseDescription = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = courseSubject,
                            onValueChange = { courseSubject = it },
                            label = { Text("Subject") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                // 构造请求体
                                val requestBody = mapOf(
                                    "name" to courseName,
                                    "description" to courseDescription,
                                    "subject" to courseSubject
                                )
                                try {
                                    val response = apiService.createCourse(requestBody)
                                    ErrorNotifier.showSuccess("Add course successful!")
                                    // 可根据返回结果处理成功逻辑
                                    showAddDialog = false
                                    // 清空表单
                                    courseName = ""
                                    courseDescription = ""
                                    courseSubject = ""
                                    // 如果需要，可刷新课程列表
                                    getAllCourses()
                                } catch (e: Exception) {
                                    ErrorNotifier.showError(e.message ?: "Add course failed")
                                }
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 课程列表
        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(courses.size) { index ->
                val course = courses[index]
                CourseCardTutor(
                    cardType = "courses",
                    course = course,
                    onConfirmClick = {}, //这里不需要这个函数
                    navController = navController
                )
            }

            // 底部加载中提示
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
