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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.tutoring.data.Course
import com.example.tutoring.ui.screens.student.common.CourseCard
import com.example.tutoring.ui.screens.tutor.common.CourseCardTutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(navController: NavHostController) {

    // 模拟分页数据
    var page by remember { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    var courses by remember { mutableStateOf(listOf<Course>()) }
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // 模拟加载数据函数
    fun loadCourses(loadMore: Boolean = false) {
        // 如果正在加载，直接返回
        if (isLoading) return
        isLoading = true

        // 模拟网络请求
        scope.launch {
            delay(1000) // 模拟网络延迟

            // 生成假数据 // TODO 需要过滤出来status = success的
            val newCourses = (1..10).map {
                val index = ((page - 1) * 10) + it
                Course(
                    id = index,
                    courseName = "Course #$index",
                    subjectName = "Subject #${index % 3 + 1}",
                    status = ""
                )
            }

            if (loadMore) {
                // 在原有列表后追加
                courses = courses + newCourses
            } else {
                // 重置列表
                courses = newCourses
            }
            page++
            isLoading = false
        }
    }

    // 首次进入页面时加载数据
    LaunchedEffect(Unit) {
        loadCourses(loadMore = false)
    }

    // 触底加载：当列表滚动到末尾时，加载下一页
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (!isLoading && lastVisibleItemIndex == courses.lastIndex) {
                    loadCourses(loadMore = true)
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
                    cardType = "courses",
                    course = course,
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
