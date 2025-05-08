package com.thierryguichardaz.xianzai.ui.feature.main

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // IMPORTANTE per lo scroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // IMPORTANTE per lo scroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.thierryguichardaz.xianzai.data.model.RecurrenceType
import com.thierryguichardaz.xianzai.ui.theme.XianZaiTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    taskDate: LocalDate,
    onTaskDateChange: (LocalDate) -> Unit,
    taskStartTime: LocalTime,
    onTaskStartTimeChange: (LocalTime) -> Unit,
    taskEndTime: LocalTime?,
    onTaskEndTimeChange: (LocalTime?) -> Unit,
    taskRecurrence: RecurrenceType,
    onTaskRecurrenceChange: (RecurrenceType) -> Unit,
    // --- NUOVI PARAMETRI ---
    taskPriority: Int,
    onTaskPriorityChange: (Int) -> Unit,
    taskCognitiveLoad: Int,
    onTaskCognitiveLoadChange: (Int) -> Unit,
    // --- FINE NUOVI PARAMETRI ---
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.ENGLISH) }
    val timeFormatter = remember { DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.ENGLISH) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    // Puoi rimuovere questi log se i picker funzionano bene
    LaunchedEffect(showDatePicker) { Log.d("AddTaskDialog", "showDatePicker state: $showDatePicker") }
    LaunchedEffect(showStartTimePicker) { Log.d("AddTaskDialog", "showStartTimePicker state: $showStartTimePicker") }
    LaunchedEffect(showEndTimePicker) { Log.d("AddTaskDialog", "showEndTimePicker state: $showEndTimePicker") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(vertical = 16.dp) // Padding verticale per dare spazio se scrolla
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()), // RENDE LA COLONNA SCROLLABILE
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Add New Task", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = taskName,
                    onValueChange = onTaskNameChange,
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = taskDate.format(dateFormatter),
                    onValueChange = { /* Non modificabile */ },
                    label = { Text("Due Date") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            Log.d("AddTaskDialog", "Date IconButton Clicked.")
                            showDatePicker = true
                        }) {
                            Icon(Icons.Filled.DateRange, "Select Date")
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = taskStartTime.format(timeFormatter),
                        onValueChange = { /* Non modificabile */ },
                        label = { Text("Start Time") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = {
                                Log.d("AddTaskDialog", "Start Time IconButton Clicked.")
                                showStartTimePicker = true
                            }) {
                                Icon(Icons.Filled.Schedule, "Select Start Time")
                            }
                        }
                    )
                    OutlinedTextField(
                        value = taskEndTime?.format(timeFormatter) ?: "Optional",
                        onValueChange = { /* Non modificabile */ },
                        label = { Text("End Time") },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = {
                                Log.d("AddTaskDialog", "End Time IconButton Clicked.")
                                showEndTimePicker = true
                            }) {
                                Icon(Icons.Filled.Schedule, "Select End Time")
                            }
                        }
                    )
                }

                var recurrenceExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = recurrenceExpanded,
                    onExpandedChange = { recurrenceExpanded = !recurrenceExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = taskRecurrence.displayName,
                        onValueChange = {},
                        label = { Text("Recurrence") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clickable { recurrenceExpanded = !recurrenceExpanded }
                    )
                    ExposedDropdownMenu(
                        expanded = recurrenceExpanded,
                        onDismissRequest = { recurrenceExpanded = false }
                    ) {
                        RecurrenceType.entries.forEach { recurrence ->
                            DropdownMenuItem(
                                text = { Text(recurrence.displayName) },
                                onClick = {
                                    onTaskRecurrenceChange(recurrence)
                                    recurrenceExpanded = false
                                }
                            )
                        }
                    }
                }

                // --- NUOVI CAMPI PER PRIORITÀ E SFORZO ---
                val priorityOptions = (1..5).toList()
                var priorityExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = priorityExpanded,
                    onExpandedChange = { priorityExpanded = !priorityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = taskPriority.toString(),
                        onValueChange = {},
                        label = { Text("Priority (1-5, 5 is highest)") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clickable { priorityExpanded = !priorityExpanded }
                    )
                    ExposedDropdownMenu(
                        expanded = priorityExpanded,
                        onDismissRequest = { priorityExpanded = false }
                    ) {
                        priorityOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.toString()) },
                                onClick = {
                                    onTaskPriorityChange(selectionOption)
                                    priorityExpanded = false
                                }
                            )
                        }
                    }
                }

                val cognitiveLoadOptions = (1..5).toList()
                var cognitiveLoadExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = cognitiveLoadExpanded,
                    onExpandedChange = { cognitiveLoadExpanded = !cognitiveLoadExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = taskCognitiveLoad.toString(),
                        onValueChange = {},
                        // Puoi cambiare il nome "Cognitive Load" se preferisci, ad es. "Effort"
                        label = { Text("Cognitive Load (1-5, 5 is hardest)") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cognitiveLoadExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .clickable { cognitiveLoadExpanded = !cognitiveLoadExpanded }
                    )
                    ExposedDropdownMenu(
                        expanded = cognitiveLoadExpanded,
                        onDismissRequest = { cognitiveLoadExpanded = false }
                    ) {
                        cognitiveLoadOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption.toString()) },
                                onClick = {
                                    onTaskCognitiveLoadChange(selectionOption)
                                    cognitiveLoadExpanded = false
                                }
                            )
                        }
                    }
                }
                // --- FINE NUOVI CAMPI ---

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = onSave, enabled = taskName.isNotBlank()) {
                        Text("Save Task")
                    }
                }
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = taskDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onTaskDateChange(LocalDate.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
        ) { DatePicker(state = datePickerState) }
    }

    // StartTimePicker Dialog
    if (showStartTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = taskStartTime.hour, initialMinute = taskStartTime.minute, is24Hour = true)
        CustomTimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTaskStartTimeChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    showStartTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartTimePicker = false }) { Text("Cancel") } }
        ) { TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth().padding(16.dp)) }
    }

    // EndTimePicker Dialog
    if (showEndTimePicker) {
        val initialEndTime = taskEndTime ?: taskStartTime.plusMinutes(30)
        val timePickerState = rememberTimePickerState(initialHour = initialEndTime.hour, initialMinute = initialEndTime.minute, is24Hour = true)
        CustomTimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTaskEndTimeChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    showEndTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = {
                    onTaskEndTimeChange(null)
                    showEndTimePicker = false
                }) { Text("Cancel (Clear)") }
            }
        ) { TimePicker(state = timePickerState, modifier = Modifier.fillMaxWidth().padding(16.dp)) }
    }
}

// CustomTimePickerDialog (invariato)
@Composable
fun CustomTimePickerDialog(title: String = "Select Time", onDismissRequest: () -> Unit, confirmButton: @Composable () -> Unit, dismissButton: @Composable (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest, title = { Text(text = title) }, text = { Column { content() } }, confirmButton = confirmButton, dismissButton = dismissButton)
}


@Preview(showBackground = true)
@Composable
fun AddTaskDialogPreview() {
    XianZaiTheme {
        var taskName by remember { mutableStateOf("Sample Task") }
        var taskDate by remember { mutableStateOf(LocalDate.now()) }
        var taskStartTime by remember { mutableStateOf(LocalTime.of(9, 0)) }
        var taskEndTime by remember { mutableStateOf<LocalTime?>(LocalTime.of(10, 30)) }
        var taskRecurrence by remember { mutableStateOf(RecurrenceType.DAILY) }
        // --- AGGIUNTE PER LA PREVIEW ---
        var taskPriority by remember { mutableStateOf(3) }
        var taskCognitiveLoad by remember { mutableStateOf(3) }

        AddTaskDialog(
            taskName = taskName, onTaskNameChange = { taskName = it },
            taskDate = taskDate, onTaskDateChange = { taskDate = it },
            taskStartTime = taskStartTime, onTaskStartTimeChange = { taskStartTime = it },
            taskEndTime = taskEndTime, onTaskEndTimeChange = { taskEndTime = it },
            taskRecurrence = taskRecurrence, onTaskRecurrenceChange = { taskRecurrence = it },
            // --- PASSAGGIO NUOVI VALORI ALLA PREVIEW ---
            taskPriority = taskPriority, onTaskPriorityChange = { taskPriority = it },
            taskCognitiveLoad = taskCognitiveLoad, onTaskCognitiveLoadChange = { taskCognitiveLoad = it },
            onDismiss = {},
            onSave = {}
        )
    }
}