package com.example.tutoring.ui.screens.tutor

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
import com.example.tutoring.ui.screens.tutor.common.CourseCardTutor
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import kotlinx.coroutines.launch

data class CourseRegistration(
    val courseId: Int = 0,
    val courseName: String = "",
    val createdAt: String? = null,  // 可为空
    val description: String = "",
    val subject: String = "",
    val tutorId: Int = 0,
    val updatedAt: String? = null,  // 可为空
    val status: String = "",
    val teacherName: String = "",
    val registrationId: Int = 0,
    val studentId: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(navController: NavHostController, loadingViewModel: LoadingViewModel = viewModel()) {
    // 全局加载指示器（overlay）
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
    // 模拟分页数据
    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var courses by remember { mutableStateOf(listOf<CourseRegistration>()) }
    var allCourses by remember { mutableStateOf(listOf<Registration>()) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    val pageSize = 10
    fun getAllCourses(){
        scope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.listAllRegistrations()
                allCourses = response.data as List<Registration>

                @Suppress("UNCHECKED_CAST")
                courses = allCourses.take(pageSize).map { registration ->
                    // 请求 detail 接口，根据 courseId 获取课程详情
                    val detailResponse = apiService.getCourseDetail(registration.courseId)
                    // 假设 detailResponse.data 为 CourseDetail 对象
                    val detail = detailResponse.data as Course
                    // 合并数据：重复的字段使用 detail 返回的值，保留 registration 中的 status
                    CourseRegistration(
                        courseId = registration.courseId,
                        courseName = detail.courseName,
                        description = detail.description,
                        tutorId = detail.tutorId,
                        subject = detail.subject,
                        status = registration.status,
                        updatedAt = detail.updatedAt,
                        createdAt = detail.createdAt,
                        registrationId = registration.registrationId,
                        studentId = registration.studentId,
                        teacherName = ""
                    )
                } ?: emptyList()
                page++
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
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
//            delay(1000) // 模拟网络延迟
// 根据页码计算起始和结束下标
            val startIndex = (page - 1) * pageSize
            val endIndex = minOf(startIndex + pageSize, allCourses.size)
// 截取当前页的数据，如果超出范围则返回空列表
            val newCourses = if (startIndex < allCourses.size) {
                allCourses.subList(startIndex, endIndex).map { registration ->
                    // 请求 detail 接口，根据 courseId 获取课程详情
                    val detailResponse = apiService.getCourseDetail(registration.courseId)
                    // 假设 detailResponse.data 为 CourseDetail 对象
                    val detail = detailResponse.data as Course
                    // 合并数据：重复的字段使用 detail 返回的值，保留 registration 中的 status
                    CourseRegistration(
                        courseId = registration.courseId,
                        courseName = detail.courseName,
                        description = detail.description,
                        tutorId = detail.tutorId,
                        subject = detail.subject,
                        status = registration.status,
                        updatedAt = detail.updatedAt,
                        createdAt = detail.createdAt,
                        registrationId = registration.registrationId,
                        studentId = registration.studentId,
                        teacherName = ""
                    )
                }
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
                CourseCardTutor(
                    cardType = "application",
                    course = course,
                    onConfirmClick = { confirm ->
                        // TODO 进行网络请求
                        scope.launch {
                            try {
                                val requestBody = mapOf(
                                    "decision" to confirm,
                                )
                                if(confirm == "approved"){
                                    val response = apiService.updateRegistration(requestBody,course.registrationId)
                                    ErrorNotifier.showSuccess("Apply Successful! Please wait for the tutor to confirm.")
                                }else if(confirm == "rejected"){
                                    val response = apiService.updateRegistration(requestBody,course.registrationId)
                                    ErrorNotifier.showSuccess("Apply Successful! Please wait for the tutor to confirm.")
                                }
                                val updated = courses.toMutableList()
                                updated[index] = updated[index].copy(status = confirm)
                                courses = updated
                            } catch (e: Exception) {
                                ErrorNotifier.showError(e.message ?: "Join Failed.")
                            }
                        }

                        val updated = courses.toMutableList()
                        updated[index] = updated[index].copy(status = "Pending")
                        courses = updated
                    },
                    navController
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
