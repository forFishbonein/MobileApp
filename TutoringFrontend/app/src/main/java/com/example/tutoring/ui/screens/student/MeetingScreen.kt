package com.example.tutoring.ui.screens.student

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoring.data.AvailabilitySlotDetail
import com.example.tutoring.data.Meeting
import com.example.tutoring.data.TutorInfo
import com.example.tutoring.network.ApiService
import com.example.tutoring.network.NetworkClient
import com.example.tutoring.utils.ErrorNotifier
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.tutoring.utils.LoadingViewModel
import com.google.gson.Gson

// 单个可用时段
data class AvailabilitySlot(
    val availabilityId: Int,   // 唯一 ID，如果是新增，可以置 0
    val startTime: String,     // 格式例如 "2025-04-26T14:00:00"
    val endTime: String        // 格式例如 "2025-04-26T15:00:00"
)

// 请求体封装
data class AvailabilityRequest(
    val availabilitySlots: List<AvailabilitySlot>
)

data class Slot(
    val availabilityId: Int,
    val startTime: String,
    val endTime: String,
    val isBooked: Boolean,
)
data class BookMeetingRequest(
    val availabilityId: Int,
    val content: String
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingScreen(
//    tutorIds: List<Int>,
    loadingViewModel: LoadingViewModel = viewModel()
) {
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val apiService = NetworkClient.createService(ApiService::class.java)
//    val tutorIds = intArrayOf(2,3,4)
    var selectedTutorId by remember { mutableStateOf<Int?>(null) }
    var selectedTutorName by remember { mutableStateOf<String?>(null) }

    var expanded by remember { mutableStateOf(false) }

    // --- State ---
    val slots = remember { mutableStateListOf<AvailabilitySlotDetail>() }
    val meetings = remember { mutableStateListOf<Meeting>() }
    // 分组：已确认 vs 其他
    val confirmed = meetings.filter { it.status == "Confirmed" }
    val others    = meetings.filter { it.status != "Confirmed" }
    var slotToBook    by remember { mutableStateOf<AvailabilitySlotDetail?>(null) }
    var bookingContent by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    // 拉可预约时段
    fun loadSlots(tutorId: Int) {
        scope.launch {
            try {
                val resp = apiService.getSlotsAvailabilityStudent(tutorId)
                slots.clear()
                slots.addAll(resp.data ?: emptyList())
            } catch (e: Exception) {
                ErrorNotifier.showError(e.message ?: "Network error")
            }
        }
    }

    // 拉学生的所有预约
    fun loadMeetings() {
        scope.launch {
            try {
                val resp = apiService.getAllMeetingsStudent()
                meetings.clear()
                meetings.addAll(resp.data ?: emptyList())
            } catch (e: Exception) {
                ErrorNotifier.showError(e.message ?: "Network error")
            }
        }
    }
//    LaunchedEffect(tutorIds) {
//        Log.d("MeetingScreen", "当前可选 tutorIds = $tutorIds")
//        // 如果非空，就做一次初始化加载
//        tutorIds.firstOrNull()?.let { loadSlots(it) }
//    }
    val tutors = remember { mutableStateListOf<TutorInfo>() }
    fun loadTutors() {
        scope.launch {
            loadingViewModel.setLoading(true)
            try {
                val resp = apiService.getAllBookableTutors()
                tutors.clear()
                tutors.addAll(resp.data ?: emptyList())
                selectedTutorId = tutors.map { it.tutorId }.firstOrNull() //设置默认的selectedTutorId
                selectedTutorName = tutors.map { it.tutorNickname }.firstOrNull() //设置默认的selectedTutorName
                loadSlots(selectedTutorId!!)
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                ErrorNotifier.showError(e.message ?: "Network error")
                loadingViewModel.setLoading(false)
            }
        }
    }
    // 首次进入加载
    LaunchedEffect(Unit) {
        loadMeetings()
        //这里需要加载 老师-老师 id的对应列表，也就是tutorIds ，同时在最后 selectedTutorId = tutorIds.firstOrNull()
        loadTutors()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // —— 下拉框：选择导师 ——
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedTutorName ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text("Select Tutor") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tutors.forEach { tutor ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    tutor.tutorNickname,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    tutor.tutorEmail, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            selectedTutorId = tutor.tutorId
                            selectedTutorName = tutor.tutorNickname
                            expanded = false
                        }
                    )
                }
            }
        }
        // ——— Available Slots ———
        Text("Available Slots (${slots.size})", style = MaterialTheme.typography.titleMedium)
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(slots, key = { it.availabilityId }) { slot ->
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = slot.startTime,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = slot.endTime,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Button(
                                onClick = { slotToBook = slot },
                                modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                            ) {
                                Text("Book")
                            }
                        }
                    }
                }
            }
        }

        // 预约确认对话框
        // —— 在这里弹出含输入框的对话框 ——
        slotToBook?.let { s ->
            AlertDialog(
                onDismissRequest = {
                    slotToBook = null
                    bookingContent = ""              // 清理输入
                },
                title = { Text("Confirm Booking") },
                text = {
                    Column {
                        Text("Book ${s.startTime} ～ ${s.endTime}?")
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = bookingContent,
                            onValueChange = { bookingContent = it },
                            label = { Text("Enter your message") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        slotToBook = null
                        coroutineScope.launch {
                            loadingViewModel.setLoading(true)
                            try {
                                val request = BookMeetingRequest(
                                    availabilityId = s.availabilityId,
                                    content = bookingContent
                                )
                                val resp = apiService.bookAMeeting(request)
                                ErrorNotifier.showSuccess("Booking successful!")
                                //重新加载所有内容，因为slots被选了就不能再选了
                                selectedTutorId?.let { loadSlots(it) }
                                loadMeetings()
                            } catch (e: Exception) {
                                ErrorNotifier.showError(e.message ?: "Network error")
                            } finally {
                                loadingViewModel.setLoading(false)
                                bookingContent = ""    // 预约完成后清空
                            }
                        }
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        slotToBook = null
                        bookingContent = ""
                    }) {
                        Text("No")
                    }
                }
            )
        }

        Divider()
        // —— 待参加会议 区块 ——
        if (confirmed.isNotEmpty()) {
            Text(
                "Upcoming Meetings (${confirmed.size})",
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(confirmed, key = { it.bookingId }) { m ->
                        MeetingCard(m)
                    }
                }
            }
        }
        Divider()
        // —— 其他会议 区块 ——
        if (others.isNotEmpty()) {
            Text(
                "My Bookings (${others.size})",
                style = MaterialTheme.typography.titleMedium
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
            ) {
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(others, key = { it.bookingId }) { m ->
                        MeetingCard(m)
                    }
                }
            }
        }

// ——— My Bookings ———
//        Text("My Bookings (${meetings.size})", style = MaterialTheme.typography.titleMedium)
//        Box(
//            Modifier
//                .fillMaxWidth()
//                .heightIn(max = 500.dp)
//        ) {
//            LazyColumn(
//                Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight(),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(meetings, key = { it.bookingId }) { m ->
//                    Card(
//                        Modifier.fillMaxWidth(),
//                        shape = MaterialTheme.shapes.medium
//                    ) {
//                        Column(Modifier.padding(12.dp)) {
//                            Text(
//                                text = "With ${m.tutorNickname}",
//                                // 或者 m.tutorNickname 取决于后端字段
//                                style = MaterialTheme.typography.bodyLarge
//                            )
//                            Spacer(Modifier.height(4.dp))
//                            Text(
//                                text = "${m.startTime} ～ ${m.endTime}",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                            Spacer(Modifier.height(4.dp))
//                            Text(
//                                text = m.status,
//                                style = MaterialTheme.typography.bodySmall
//                            )
//                        }
//                    }
//                }
//            }
//        }
        }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingCard(meeting: Meeting) {
    Card(
        Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "With ${meeting.tutorNickname}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${meeting.startTime} ～ ${meeting.endTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            // 状态区，用 Row 撑满宽度，然后把 Chip 推到最右侧
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                AssistChip(
                    onClick = { /* 不可点击 */ },
                    label = { Text(meeting.status) },
                    enabled = false, //false的时候强制是灰色，不会展示颜色
                    // 如果想让它更往右一点，可以再加个 offset
                    modifier = Modifier
                        .defaultMinSize(minHeight = 32.dp)
                        .offset(x = 4.dp),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (meeting.status) {
                            "Confirmed" -> MaterialTheme.colorScheme.primaryContainer
                            "Pending"   -> MaterialTheme.colorScheme.secondaryContainer
                            "Cancelled" -> MaterialTheme.colorScheme.errorContainer
                            else        -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        labelColor = when (meeting.status) {
                            "Confirmed" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "Pending"   -> MaterialTheme.colorScheme.onSecondaryContainer
                            "Cancelled" -> MaterialTheme.colorScheme.onErrorContainer
                            else        -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                )
            }

        }
    }
}