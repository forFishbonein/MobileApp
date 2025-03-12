package com.example.tutoring.ui.screens.tutor
import android.annotation.SuppressLint
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tutoring.data.Lesson
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.ui.navigation.tutor.TutorNavRoutes
import com.example.tutoring.ui.screens.tutor.common.LessonCardTutor
import com.example.tutoring.utils.ErrorNotifier
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPagerApi::class) // Accompanist Pager 注解
@Composable
fun LessonsScreen(courseId: Int?, navController: NavHostController) {
    // TODO 根据courseId去查找对应的 course 内容
    // 模拟一组 lesson 数据
//    val lessons = listOf(
//        Lesson(1, "Lesson 1", "doing", "<h1>Lesson Title</h1>\n" +
//                "  <p>This lesson covers the basic concepts of the topic. You can include any rich text content here, such as formatted paragraphs, images, or lists.</p>\n" +
//                "  <p><strong>Key Points:</strong></p>\n" +
//                "  <ul>\n" +
//                "    <li>Introduction to the topic</li>\n" +
//                "    <li>Explanation of core concepts</li>\n" +
//                "    <li>Examples and exercises</li>\n" +
//                "  </ul>" + "<h1>Lesson Title</h1>\n" +
//                "  <p>This lesson covers the basic concepts of the topic. You can include any rich text content here, such as formatted paragraphs, images, or lists.</p>\n" +
//                "  <p><strong>Key Points:</strong></p>\n" +
//                "  <ul>\n" +
//                "    <li>Introduction to the topic</li>\n" +
//                "    <li>Explanation of core concepts</li>\n" +
//                "    <li>Examples and exercises</li>\n" +
//                "  </ul>" + "<h1>Lesson Title</h1>\n" +
//                "  <p>This lesson covers the basic concepts of the topic. You can include any rich text content here, such as formatted paragraphs, images, or lists.</p>\n" +
//                "  <p><strong>Key Points:</strong></p>\n" +
//                "  <ul>\n" +
//                "    <li>Introduction to the topic</li>\n" +
//                "    <li>Explanation of core concepts</li>\n" +
//                "    <li>Examples and exercises</li>\n" +
//                "  </ul>"
//        ),
//        Lesson(2, "Lesson 2", "doing", "This is the second lesson."),
//        Lesson(3, "Lesson 3", "doing", "This is the third lesson."),
//    )
    var lessons by remember { mutableStateOf(listOf<Lesson>()) }
    // PagerState 控制当前显示第几页
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
    fun getAllLessons(){
        coroutineScope.launch {
            try {
                val response = apiService.listLessons(courseId)
                lessons = response.data as List<Lesson>
            } catch (e: Exception) {
                ErrorNotifier.showError(e.message ?: "Failed.")
            }
        }
    }
    // 首次进入页面时加载数据
    LaunchedEffect(Unit) {
        getAllLessons()
    }
//    Box() {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState()) // 允许滚动
        ) {
            // 导航按钮 Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // 点击返回上一课时
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
                        // 点击跳转到下一课时，仅当当前 lesson 的状态为 "completed" 时允许
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
            // 创建一个横向 Pager
            HorizontalPager(
                state = pagerState,
                count = lessons.size,
                modifier = Modifier
                    .fillMaxWidth(),
//                    .height(575.dp),   // 固定高度，比如 400dp，可根据需要调整
//                    .padding(horizontal = 16.dp),
                userScrollEnabled = true // 禁止手势滑动，必须点击按钮
            ) { pageIndex ->
                val lesson = lessons[pageIndex]
                // Card 内容
                LessonCardTutor(
                    lesson = lesson,
                    onChangeComplete = {
                        // TODO 进行网络请求
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
                // 在 Box 内右下角添加“Add New Course”按钮
                Button(
                    onClick = {
                        /* 添加新课程的逻辑，比如导航到添加课程页面 */
                        navController.navigate("tutor_add_lesson?courseId=${courseId}&lesson=")
                    },
                    shape = RoundedCornerShape(8.dp), // 方形按钮带圆角
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text("Add New Lesson")
                }
            }
        }

//    }
}
