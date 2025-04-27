package com.example.tutoring.ui.screens.tutor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
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
import com.example.tutoring.data.Lesson
import com.example.tutoring.data.LessonsProcess
import com.example.tutoring.data.Meeting
import com.example.tutoring.data.UserInfo
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
// 在 SlotItem 或者全局定义：
@RequiresApi(Build.VERSION_CODES.O)
private val slotFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingScreen(loadingViewModel: LoadingViewModel = viewModel()) {
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
    // --- 日期选择相关 state ---
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            selectedDate = LocalDate.of(year, month + 1, day)
        },
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    ).apply {
        // 限制只能选择今天或之后的日期
        datePicker.minDate = Calendar.getInstance().timeInMillis
    }

    // --- 时间选择相关 state ---
    // 只存时分
    var startTime by remember { mutableStateOf("Select Start") }
    var endTime   by remember { mutableStateOf("Select End") }

    // 默认用当前时刻做弹窗初始值
    val cal = Calendar.getInstance()
    val hour0   = cal.get(Calendar.HOUR_OF_DAY)
    val minute0 = cal.get(Calendar.MINUTE)

    // 开始时段选择器
    val startPicker = TimePickerDialog(
        context,
        { _, h, m ->
            startTime = "%02d:%02d".format(h, m)
        },
        hour0, minute0, true
    )
    // 结束时段选择器
    val endPicker = TimePickerDialog(
        context,
        { _, h, m ->
            endTime = "%02d:%02d".format(h, m)
        },
        hour0, minute0, true
    )

    // --- Slot 列表  ---
    val slots = remember { mutableStateListOf<Slot>() }
    var nextId by remember { mutableStateOf(1) }
    // 新增：记录哪一条 slot 正在被请求删除
    var slotToDelete by remember { mutableStateOf<Slot?>(null) }
    fun getAllSlots(){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.getSlotsAvailabilityTutor()
                response.data?.forEach { avail ->
                    slots += Slot(
                        availabilityId = avail.availabilityId,
                        startTime      = avail.startTime,  // ISO 格式
                        endTime        = avail.endTime,
                        isBooked       = avail.isBooked             // 或根据后端字段决定
                    )
                    nextId = maxOf(nextId, avail.availabilityId + 1)
                }
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
                ErrorNotifier.showError(e.message ?: "Failed.")
            }
        }
    }
    // 示例会议列表，替换成你自己的数据
    var meetings = remember { mutableStateListOf<Meeting>() }
    // 哪个 meeting 在请求批准／拒绝
    var meetingToApprove by remember { mutableStateOf<Meeting?>(null) }
    var meetingToReject  by remember { mutableStateOf<Meeting?>(null) }
    fun getAllMeetings(){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.getAllMeetingsTutor()
                // 先清空旧数据
                meetings.clear()
                // 再把后端列表一次性加进来
                meetings.addAll(response.data ?: emptyList())
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
                ErrorNotifier.showError(e.message ?: "Failed.")
            }
        }
    }
    // --- 启动时从服务端拉初始数据 ---
    LaunchedEffect(Unit) {
        getAllSlots()
        getAllMeetings()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())   // 整体可滚动
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 日期按钮
        OutlinedButton(onClick = { datePicker.show() }) {
            Text("Date: ${selectedDate.format(dateFormatter)}")
        }

        // 时间选择 + 添加按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { startPicker.show() },
                modifier = Modifier.weight(1f)
            ) {
                Text(startTime)
            }
            OutlinedButton(
                onClick = { endPicker.show() },
                modifier = Modifier.weight(1f)
            ) {
                Text(endTime)
            }
            Button(
                onClick = {
                    // 只有当用户选了开始和结束时间时才添加
                    if (startTime != "Select Start" && endTime != "Select End") {
                        // 当用户点击“Add Slot”时，构造一个完整的 ISO 格式日期时间
                        val startDateTime = "${selectedDate.format(dateFormatter)} $startTime:00"
                        val endDateTime   = "${selectedDate.format(dateFormatter)} $endTime:00"
                        // 如果需要，还可以加一个校验：开始时间必须早于结束时间
                        val dtStart = LocalDateTime.parse(startDateTime, slotFormatter)
                        val dtEnd   = LocalDateTime.parse(endDateTime,   slotFormatter)
                        if (dtStart.isBefore(dtEnd)) {
                            slots += Slot(
                                availabilityId = nextId++,
                                startTime     = startDateTime,
                                endTime       = endDateTime,
                                isBooked      = false
                            )
                            // 重置按钮文字
                            startTime = "Select Start"
                            endTime   = "Select End"
                        } else {
                            ErrorNotifier.showError("The end time must be later than the start time.")
                        }
                    }
                }
            ) {
                Text("Add Slot")
            }

        }

        Divider()

        // --- All Slots 区块 ---
        Text(
            "All Slots (${slots.size})",
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .heightIn(max = 240.dp)     // 限定高度，可根据需求调整
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()               // 根据子项自动撑高
                    .align(Alignment.TopStart),        // 可选：让列表内容靠上
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = slots,
                    key = { it.availabilityId }
                ) { slot ->
                    SlotItem(
                        slot = slot,
                        onDeleteRequest = { slotToDelete = slot }  // 只发起“删除请求”
                    )
                }
            }

            // 根据 slotToDelete 显示确认对话框
            slotToDelete?.let { slot ->
                AlertDialog(
                    onDismissRequest = { slotToDelete = null },
                    title = { Text("Confirm deletion?") },
                    text  = {
                        // 你也可以格式化成更友好的：日期 + 时间区间
                        Text("Are you sure you want to delete the time period of ${slot.startTime} - ${slot.endTime}?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            slots.remove(slot)
                            slotToDelete = null
                        }) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { slotToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
//        if (slots.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Button(
                    onClick = {
                        val availabilitySlots = slots.map { slot ->
                            AvailabilitySlot(
                                availabilityId = slot.availabilityId,
                                startTime = "${slot.startTime}:00",  // ISO 格式字符串
                                endTime   = "${slot.endTime}:00"
                            )
                        }
                        val request = AvailabilityRequest(availabilitySlots)
                        coroutineScope.launch {
//                    loadingViewModel.setLoading(true)
                            try {
                                val response = apiService.updateSlotsAvailability(request)
//                        loadingViewModel.setLoading(false)
                                ErrorNotifier.showSuccess("Save Successful!")
                            } catch (e: Exception) {
//                        loadingViewModel.setLoading(false)
                                ErrorNotifier.showError(e.message ?: "Failed.")
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 4.dp)
                ) {
                    Text("Save")
                }
            }
//        }
        Divider()
        // --- All Meetings 区块 ---
        Text("All Meetings (${meetings.size})", style = MaterialTheme.typography.titleMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)   // 最大高度 240dp，内容少时自适应
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(meetings, key = { it.bookingId }) { meeting ->
                    MeetingItem(
                        meeting = meeting,
                        onApproveRequest = { meetingToApprove = meeting },
                        onRejectRequest  = { meetingToReject  = meeting }
                    )
                }
            }
        }
// Approve 确认框
        meetingToApprove?.let { m ->
            AlertDialog(
                onDismissRequest = { meetingToApprove = null },
                title   = { Text("Approve meeting?") },
                text    = { Text("Approve booking from ${m.studentNickname} on ${m.startTime}?") },
                confirmButton = {
                    TextButton(onClick = {
                        meetingToApprove = null
                        coroutineScope.launch {
                            try {
                                val response = apiService.approveMeeting(m.bookingId)
                                val idx = meetings.indexOfFirst { it.bookingId == m.bookingId }
                                if (idx >= 0) {
                                    meetings[idx] = meetings[idx].copy(status = "Confirmed")
                                }
                                ErrorNotifier.showSuccess("Approve Successful!")
                            } catch (e: Exception) {
                                ErrorNotifier.showError(e.message ?: "Failed.")
                            }
                        }
                    }) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(onClick = { meetingToApprove = null }) { Text("No") }
                }
            )
        }

        // Reject 确认框
        meetingToReject?.let { m ->
            AlertDialog(
                onDismissRequest = { meetingToReject = null },
                title   = { Text("Reject meeting?") },
                text    = { Text("Reject booking from ${m.studentNickname} on ${m.startTime}?") },
                confirmButton = {
                    TextButton(onClick = {
                        meetingToReject = null
                        coroutineScope.launch {
                            try {
                                val response = apiService.rejectMeeting(m.bookingId)
                                val idx = meetings.indexOfFirst { it.bookingId == m.bookingId }
                                if (idx >= 0) {
                                    meetings[idx] = meetings[idx].copy(status = "Cancelled")
                                }
                                ErrorNotifier.showSuccess("Reject Successful!")
                            } catch (e: Exception) {
                                ErrorNotifier.showError(e.message ?: "Failed.")
                            }
                        }
                    }) { Text("Yes") }
                },
                dismissButton = {
                    TextButton(onClick = { meetingToReject = null }) { Text("No") }
                }
            )
        }

    }

}


@Composable
private fun SlotItem(
    slot: Slot,
    onDeleteRequest: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = slot.startTime, style = MaterialTheme.typography.bodyLarge)
                Text(text = slot.endTime,   style = MaterialTheme.typography.bodyLarge)
            }

            Text(
                text = if (slot.isBooked) "Booked" else "Available",
                color = if (slot.isBooked) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 只有 isBooked == false 时才给删除按钮留空间
            if (!slot.isBooked) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDeleteRequest) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete slot"
                    )
                }
            }
        }
    }
}
@Composable
fun MeetingItem(
    meeting: Meeting,
    onApproveRequest: () -> Unit,
    onRejectRequest: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = "${meeting.studentNickname}  (${meeting.status})",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${meeting.startTime} ～ ${meeting.endTime}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = meeting.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (meeting.status == "Pending") {
                Spacer(Modifier.height(8.dp))
                // 用 Box + Row 右对齐
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onRejectRequest,
                            modifier = Modifier
                                .wrapContentWidth()
                                .defaultMinSize(minWidth = 64.dp, minHeight = 32.dp)
                        ) {
                            Text("Reject")
                        }
                        Button(
                            onClick = onApproveRequest,
                            modifier = Modifier
                                .wrapContentWidth()
                                .defaultMinSize(minWidth = 64.dp, minHeight = 32.dp)
                        ) {
                            Text("Approve")
                        }
                    }
                }
            }
        }
    }
}