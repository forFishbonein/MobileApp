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
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoring.data.CourseProgress
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import com.example.tutoring.utils.LoadingViewModel
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import com.github.tehras.charts.piechart.animation.simpleChartAnimation
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

        // —— The dashboard of selected courses ——
        selectedCourse?.let { course ->
            Text(
                "Enrolled Students: ${progressList?.students?.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            // progress chart
//            progressList?.students?.let { StudentProgressChart(progressList = it) }
//            progressList?.students?.let { ProgressCountBarChart(progressList = it) }
            // Render the first progress diagram first (if there are students)
            progressList?.students
                .takeIf { !it.isNullOrEmpty() }
                ?.let { students ->
                    StudentProgressChart(progressList = students)

                    Spacer(modifier = Modifier.height(24.dp))

                    // 在渲染第二个图表前加个标题
                    Text(
                        text = "Completion Distribution",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ProgressCountBarChart(progressList = students)
                }
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

//@Composable
//fun ProgressCountBarChart(
//    progressList: List<StudentProgress>,
//    modifier: Modifier = Modifier
//) {
//    /* ---------- 数据统计 ---------- */
//    val counts = remember(progressList) {
//        progressList.groupingBy { it.progressPercent }
//            .eachCount()
//            .toSortedMap()                     // 0 → 25 → 100
//    }
//
//    val bars = counts.map { (percent, cnt) ->
//        BarChartData.Bar(
//            label = "$percent%",               // X 轴文字
//            value = cnt.toFloat(),             // 人数
//            color = when {
//                percent >= 100 -> Color(0xFF4CAF50)
//                percent >= 50  -> Color(0xFFFFA000)
//                else           -> Color(0xFFD32F2F)
//            }
//        )
//    }
//    val data = BarChartData(bars = bars)
//
//    /* ---------- Y 轴整数刻度 ---------- */
//    val maxCount = counts.values.maxOrNull() ?: 1
//    // ❶ 只生成整数刻度 [0f, 1f, …, maxCount.toFloat()]
//    val yLabelValues = (0..maxCount).map { it.toFloat() }
//
//    BarChart(
//        barChartData = data,
//        modifier     = modifier
//            .fillMaxWidth()
//            .height(300.dp),
//
//        barDrawer    = SimpleBarDrawer(),
//        xAxisDrawer  = SimpleXAxisDrawer(),    // 库自带，直接用
//
//        // ❷ 用 labelValues 指定刻度位置
//        // ❸ labelFormatter 把浮点转成整数字符串
//        yAxisDrawer  = SimpleYAxisDrawer(),
//
//        // ❹ 柱顶人数标签
//        labelDrawer  = SimpleValueDrawer(),
//    )
//}

@Composable
fun ProgressCountBarChart(
    progressList: List<StudentProgress>,
    modifier: Modifier = Modifier,
    barWidthDp: Dp = 28.dp,
    axisStroke: Float = 1f             // width of the axis lines
) {
    /* ---------- Count the number of students per progress percentage ---------- */
    val counts = remember(progressList) {
        progressList.groupingBy { it.progressPercent }
            .eachCount()
            .toSortedMap()                 // ensures keys are in order: 0 → 25 → 100
    }
    val maxCount = counts.values.maxOrNull() ?: 1

    /* ---------- Compute dimensions in pixels ---------- */
    val density = LocalDensity.current
    val barWidthPx   = with(density) { barWidthDp.toPx() }
    val xLabelHeight = with(density) { 18.dp.toPx() }
    val topPadding   = with(density) { 12.dp.toPx() }
    val yAxisWidth   = with(density) { 36.dp.toPx() }   // left margin for Y-axis labels

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)                // total component height, adjust as needed
            .padding(horizontal = 12.dp)
    ) {
        /* ---------- Define the drawing area ---------- */
        val chartLeft   = yAxisWidth
        val chartRight  = size.width
        val chartBottom = size.height - xLabelHeight
        val chartTop    = topPadding
        val chartHeight = chartBottom - chartTop

        /* ---------- Draw Y-axis integer ticks & horizontal grid lines ---------- */
        val yStep = 1                     // one person per tick; change to 2 or 5 for sparser ticks
        val steps = ((maxCount + yStep - 1) / yStep).coerceAtLeast(1)
        val stepPx = chartHeight / steps

        val textPaint = android.graphics.Paint().apply {
            color       = android.graphics.Color.BLACK
            textSize    = with(density) { 12.sp.toPx() }
            textAlign   = android.graphics.Paint.Align.RIGHT
            isAntiAlias = true
        }

        for (i in 0..steps) {
            val y = chartBottom - i * stepPx
            // draw horizontal grid line
            drawLine(
                color       = Color.LightGray,
                start       = Offset(chartLeft, y),
                end         = Offset(chartRight, y),
                strokeWidth = axisStroke
            )
            // draw integer tick label
            val label = (i * yStep).toString()
            drawContext.canvas.nativeCanvas.drawText(
                label,
                /* x */ chartLeft - 4.dp.toPx(),
                /* y */ y + textPaint.textSize / 2,   // vertically center text
                textPaint
            )
        }

        /* ---------- Draw the bars ---------- */
        val slotWidth = (chartRight - chartLeft) / counts.size
        counts.values.forEachIndexed { idx, count ->
            val percent = counts.keys.elementAt(idx)
            // calculate bar rectangle
            val barLeft   = chartLeft + idx * slotWidth + (slotWidth - barWidthPx) / 2
            val barHeight = if (maxCount == 0)
                0f else chartHeight * count / maxCount.toFloat()
            val barTop    = chartBottom - barHeight

            // choose color based on progress
            val barColor = when {
                percent >= 100 -> Color(0xFF4CAF50)
                percent >= 50  -> Color(0xFFFFA000)
                else           -> Color(0xFFD32F2F)
            }

            // draw the bar
            drawRect(
                color   = barColor,
                topLeft = Offset(barLeft, barTop),
                size    = Size(barWidthPx, barHeight)
            )

            // draw the count label above the bar
            val countLabel = "$count people"
            drawContext.canvas.nativeCanvas.drawText(
                countLabel,
                barLeft + barWidthPx / 2,
                barTop - 4.dp.toPx(),
                textPaint.apply { textAlign = android.graphics.Paint.Align.CENTER }
            )

            // draw the X-axis percentage label
            drawContext.canvas.nativeCanvas.drawText(
                "$percent%",
                barLeft + barWidthPx / 2,
                chartBottom + xLabelHeight - 4.dp.toPx(),
                textPaint
            )
        }

        /* ---------- Draw the Y and X axes ---------- */
        // Y-axis line
        drawLine(
            color       = Color.Black,
            start       = Offset(chartLeft, chartTop),
            end         = Offset(chartLeft, chartBottom),
            strokeWidth = axisStroke
        )
        // X-axis line
        drawLine(
            color       = Color.Black,
            start       = Offset(chartLeft, chartBottom),
            end         = Offset(chartRight, chartBottom),
            strokeWidth = axisStroke
        )
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DashboardScreenPreview() {
//    TutorDashboardScreen()
//}
