@file:OptIn(ExperimentalFoundationApi::class)
package com.example.tutoring.ui.screens.tutor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
//import androidx.compose.foundation.VerticalScrollbar
//import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.lazy.rememberLazyListState

data class AvailabilitySlot(
    val availabilityId: Int,
    val startTime: String,
    val endTime: String
)

data class AvailabilityRequest(
    val availabilitySlots: List<AvailabilitySlot>
)

data class Slot(
    val availabilityId: Int,
    val startTime: String,
    val endTime: String,
    val isBooked: Boolean,
)
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
        // The restriction is that only today or later dates can be selected
        datePicker.minDate = Calendar.getInstance().timeInMillis
    }

    // --- time select state ---
    var startTime by remember { mutableStateOf("Select Start") }
    var endTime   by remember { mutableStateOf("Select End") }

    val cal = Calendar.getInstance()
    val hour0   = cal.get(Calendar.HOUR_OF_DAY)
    val minute0 = cal.get(Calendar.MINUTE)

    val startPicker = TimePickerDialog(
        context,
        { _, h, m ->
            startTime = "%02d:%02d".format(h, m)
        },
        hour0, minute0, true
    )
    val endPicker = TimePickerDialog(
        context,
        { _, h, m ->
            endTime = "%02d:%02d".format(h, m)
        },
        hour0, minute0, true
    )

    // --- Slot list  ---
    val slots = remember { mutableStateListOf<Slot>() }
    var nextId by remember { mutableStateOf(1) }
    var slotToDelete by remember { mutableStateOf<Slot?>(null) }
    fun getAllSlots(){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.getSlotsAvailabilityTutor()
                response.data?.forEach { avail ->
                    slots += Slot(
                        availabilityId = avail.availabilityId,
                        startTime      = avail.startTime,
                        endTime        = avail.endTime,
                        isBooked       = avail.isBooked
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
    val listState = rememberLazyListState()
    var meetings = remember { mutableStateListOf<Meeting>() }
    var meetingToApprove by remember { mutableStateOf<Meeting?>(null) }
    var meetingToReject  by remember { mutableStateOf<Meeting?>(null) }
    fun getAllMeetings(){
        coroutineScope.launch {
            loadingViewModel.setLoading(true)
            try {
                val response = apiService.getAllMeetingsTutor()
                meetings.clear()
                meetings.addAll(response.data ?: emptyList())
                loadingViewModel.setLoading(false)
            } catch (e: Exception) {
                loadingViewModel.setLoading(false)
                ErrorNotifier.showError(e.message ?: "Failed.")
            }
        }
    }
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
        // Date button
        OutlinedButton(onClick = { datePicker.show() }) {
            Text("Date: ${selectedDate.format(dateFormatter)}")
        }

        // Time selection add button
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
                    // It is added only when the user selects the start and end times
                    if (startTime != "Select Start" && endTime != "Select End") {
                        // When the user clicks "Add Slot", construct a complete ISO format date and time
                        val startDateTime = "${selectedDate.format(dateFormatter)} $startTime:00"
                        val endDateTime   = "${selectedDate.format(dateFormatter)} $endTime:00"
//                        val startDateTime = "${selectedDate.format(dateFormatter)} $startTime"
//                        val endDateTime   = "${selectedDate.format(dateFormatter)} $endTime"
                        val dtStart = LocalDateTime.parse(startDateTime, slotFormatter)
                        val dtEnd   = LocalDateTime.parse(endDateTime,   slotFormatter)
                        //The start time must be earlier than the end time
                        if (dtStart.isBefore(dtEnd)) {
                            slots += Slot(
                                availabilityId = nextId++,
//                                startTime     = startDateTime,
//                                endTime       = endDateTime,
                                startTime  = dtStart.toString().replace('T', ' '),
                                endTime    = dtEnd.toString().replace('T', ' '),
                                isBooked      = false
                            )
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

        // --- All Slots block ---
        Text(
            "All Slots (${slots.size})",
            style = MaterialTheme.typography.titleMedium
        )
        Box(
            modifier = Modifier
                .heightIn(max = 260.dp)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,            // ← 关键：把 state 传给 LazyColumn
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = slots,
                    key = { it.availabilityId }
                ) { slot ->
                    SlotItem(
                        slot = slot,
                        onDeleteRequest = {
//                            slotToDelete = slot
                        }
                    )
                }
            }
//            VerticalScrollbar(
//                adapter = rememberScrollbarAdapter(listState),
//                modifier = Modifier
//                    .align(Alignment.CenterEnd)
//                    .fillMaxHeight()
//            )
            // 根据 slotToDelete 显示确认对话框
            slotToDelete?.let { slot ->
                AlertDialog(
                    onDismissRequest = { slotToDelete = null },
                    title = { Text("Confirm deletion?") },
                    text  = {
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
                                startTime = "${slot.startTime}:00",  // ISO format
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
        // --- All Meetings block ---
        Text("All Meetings (${meetings.size})", style = MaterialTheme.typography.titleMedium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
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
        // Approve confirmation box
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

        // Reject confirmation box
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

            // Space is only left for the delete button when isBooked == false
//            if (!slot.isBooked) {
//                Spacer(modifier = Modifier.width(8.dp))
//                IconButton(onClick = onDeleteRequest) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Delete slot"
//                    )
//                }
//            }
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