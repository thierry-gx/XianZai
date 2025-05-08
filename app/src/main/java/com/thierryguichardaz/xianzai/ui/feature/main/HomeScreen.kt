@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTextApi::class)

package com.thierryguichardaz.xianzai.ui.feature.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check // Icona spunta semplice
import androidx.compose.material.icons.filled.ErrorOutline // Icona per task scaduti
import androidx.compose.material.icons.filled.Event // Icona generica per data
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker // Verifica questo
import androidx.compose.material3.DatePickerDialog // Verifica questo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker // Verifica questo
import androidx.compose.material3.rememberDatePickerState // Verifica questo
import androidx.compose.material3.rememberTimePickerState // Verifica questo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha // Per la trasparenza
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration // Per barrare il testo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thierryguichardaz.xianzai.data.model.Task
import com.thierryguichardaz.xianzai.data.preferences.DEFAULT_CHRONOTYPE
import com.thierryguichardaz.xianzai.data.preferences.UserPreferencesRepository
import com.thierryguichardaz.xianzai.ui.theme.XianZaiTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.thierryguichardaz.xianzai.data.model.RecurrenceType
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle



// Data per l'energia (invariata)
val definiteMorningEnergyLevels = listOf(3.0f, 4.0f, 5.0f, 7.0f, 8.5f, 9.5f, 10.0f, 9.5f, 8.5f, 7.5f, 6.5f, 5.5f, 5.0f, 4.5f, 4.0f, 3.5f, 3.0f, 2.5f, 2.0f, 1.5f, 1.0f, 0.5f, 0.5f, 0.0f)
val moderateMorningEnergyLevels = listOf(2.0f, 2.5f, 3.5f, 5.0f, 6.5f, 8.0f, 9.0f, 9.5f, 9.0f, 8.0f, 7.0f, 6.0f, 5.5f, 5.0f, 5.0f, 5.5f, 5.0f, 4.5f, 4.0f, 3.5f, 2.5f, 2.0f, 1.5f, 1.0f)
val intermediateEnergyLevels = listOf(1.0f, 1.5f, 2.0f, 3.0f, 4.0f, 5.0f, 6.5f, 7.5f, 8.5f, 9.0f, 9.5f, 8.5f, 7.5f, 6.5f, 6.0f, 6.5f, 7.0f, 7.5f, 7.0f, 6.0f, 5.0f, 4.0f, 3.0f, 2.0f)
val moderateEveningEnergyLevels = listOf(0.5f, 0.5f, 1.0f, 1.5f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 7.5f, 8.0f, 8.5f, 8.0f, 8.5f, 9.0f, 9.5f, 9.0f, 8.5f, 7.5f, 7.0f, 6.0f, 5.0f, 4.0f)
val definiteEveningEnergyLevels = listOf(0.0f, 0.0f, 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 7.5f, 8.0f, 8.5f, 9.0f, 9.5f, 10.0f, 9.5f, 9.0f, 8.5f, 8.0f, 7.0f, 6.0f)

fun getEnergyDataForChronotype(chronotype: String): List<Float> {
    return when (chronotype) {
        "Definite Morning" -> definiteMorningEnergyLevels
        "Moderate Morning" -> moderateMorningEnergyLevels
        "Intermediate" -> intermediateEnergyLevels
        "Moderate Evening" -> moderateEveningEnergyLevels
        "Definite Evening" -> definiteEveningEnergyLevels
        else -> intermediateEnergyLevels
    }
}

fun getCurrentHour(): Int {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

// --- HELPER FUNCTIONS FOR TASKS (ENGLISH STRINGS) ---
// --- NUOVI HELPER PER FORMATTARE LocalDate e LocalTime ---
fun formatDisplayDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.ENGLISH)
    return date.format(formatter)
}

fun formatDisplayTime(time: LocalTime): String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.ENGLISH)
    return time.format(formatter)
}

// Controlla se un task NON completato è scaduto per la visualizzazione
fun isTaskDisplayOverdue(dueDate: LocalDate): Boolean { // Modificato per accettare LocalDate
    return dueDate.isBefore(LocalDate.now(ZoneId.systemDefault()))
}


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(UserPreferencesRepository(LocalContext.current))
    )
) {
    val chronotype by homeViewModel.userChronotype.collectAsStateWithLifecycle()
    val tasks by homeViewModel.tasks.collectAsStateWithLifecycle()

    // Stati per il dialog dal ViewModel
    val showDialog by homeViewModel.showAddTaskDialog.collectAsStateWithLifecycle()
    val taskName by homeViewModel.taskFormName.collectAsStateWithLifecycle()
    val taskDate by homeViewModel.taskFormDueDate.collectAsStateWithLifecycle()
    val taskStartTime by homeViewModel.taskFormStartTime.collectAsStateWithLifecycle()
    val taskEndTime by homeViewModel.taskFormEndTime.collectAsStateWithLifecycle()
    val taskRecurrence by homeViewModel.taskFormRecurrence.collectAsStateWithLifecycle()
    // --- NUOVI STATI DAL VIEWMODEL ---
    val taskPriority by homeViewModel.taskFormPriority.collectAsStateWithLifecycle()
    val taskCognitiveLoad by homeViewModel.taskFormCognitiveLoad.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { homeViewModel.openAddTaskDialog() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Task")
            }
            Button(
                onClick = { /* TODO: Implement Add Event action */ },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Event")
            }
        }
        EnergyCard(chronotype = chronotype)
        CurrentTasksCard(
            tasks = tasks,
            onToggleTaskCompletion = homeViewModel::toggleTaskCompletion
        )
    }

    // Mostra il dialog qui, se lo stato showDialog è true
    if (showDialog) {
        AddTaskDialog(
            taskName = taskName,
            onTaskNameChange = { homeViewModel.taskFormName.value = it },
            taskDate = taskDate,
            onTaskDateChange = { homeViewModel.taskFormDueDate.value = it },
            taskStartTime = taskStartTime,
            onTaskStartTimeChange = { homeViewModel.taskFormStartTime.value = it },
            taskEndTime = taskEndTime,
            onTaskEndTimeChange = { homeViewModel.taskFormEndTime.value = it },
            taskRecurrence = taskRecurrence,
            onTaskRecurrenceChange = { homeViewModel.taskFormRecurrence.value = it },
            // --- PASSAGGIO NUOVI VALORI E CALLBACK ---
            taskPriority = taskPriority,
            onTaskPriorityChange = { homeViewModel.taskFormPriority.value = it },
            taskCognitiveLoad = taskCognitiveLoad,
            onTaskCognitiveLoadChange = { homeViewModel.taskFormCognitiveLoad.value = it },
            // --- FINE PASSAGGIO ---
            onDismiss = { homeViewModel.dismissAddTaskDialog() },
            onSave = { homeViewModel.saveNewTask() }
        )
    }
}


class HomeViewModelFactory(private val repository: UserPreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun EnergyCard(chronotype: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your Current Energy",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val currentHour = remember { getCurrentHour() }
            val energyData = remember(chronotype) {
                getEnergyDataForChronotype(chronotype)
            }

            val graphHeight = 150.dp
            val labelSpace = 20.dp

            EnergyGraph(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(graphHeight + labelSpace)
                    .padding(top = 8.dp, bottom = labelSpace),
                energyLevels = energyData,
                currentHourIndex = currentHour
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun EnergyGraph(
    modifier: Modifier = Modifier,
    energyLevels: List<Float>,
    currentHourIndex: Int,
    graphColor: Color = MaterialTheme.colorScheme.primary,
    markerColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    val maxEnergy = remember(energyLevels) { energyLevels.maxOrNull() ?: 1f }
    val safeMaxEnergy = if (maxEnergy == 0f) 1f else maxEnergy

    Canvas(modifier = modifier.fillMaxWidth()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val numPoints = energyLevels.size

        if (numPoints < 2) return@Canvas

        val pointSpacing = canvasWidth / (numPoints - 1).toFloat()

        val linePath = Path()
        val fillPath = Path()

        val firstX = 0f
        val firstY = canvasHeight - (energyLevels[0] / safeMaxEnergy) * canvasHeight
        linePath.moveTo(firstX, firstY)
        fillPath.moveTo(firstX, canvasHeight)
        fillPath.lineTo(firstX, firstY)

        energyLevels.forEachIndexed { index, energy ->
            if (index > 0) {
                val x = index * pointSpacing
                val y = canvasHeight - (energy / safeMaxEnergy) * canvasHeight
                linePath.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo((numPoints - 1) * pointSpacing, canvasHeight)
        fillPath.close()

        drawPath(
            path = fillPath,
            color = graphColor.copy(alpha = 0.3f)
        )

        drawPath(
            path = linePath,
            color = graphColor,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        val labelOffsetY = 4.dp.toPx()
        for (index in 0 until numPoints) {
            if (index == 0 || index == 6 || index == 12 || index == 18 || index == numPoints -1) {
                val hourText = "%02d".format(index)
                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(hourText),
                    style = labelStyle
                )
                val labelX = index * pointSpacing - (textLayoutResult.size.width / 2f)
                val labelY = canvasHeight + labelOffsetY

                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(x = labelX.coerceIn(0f, canvasWidth - textLayoutResult.size.width), y = labelY)
                )
            }
        }

        if (currentHourIndex >= 0 && currentHourIndex < numPoints) {
            val currentX = currentHourIndex * pointSpacing
            val currentY = canvasHeight - (energyLevels[currentHourIndex] / safeMaxEnergy) * canvasHeight
            drawCircle(
                color = markerColor,
                radius = 12f,
                center = Offset(currentX, currentY)
            )
            drawCircle(
                color = graphColor,
                radius = 12f,
                center = Offset(currentX, currentY),
                style = Stroke(width = 4f)
            )
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onToggleCompletion: () -> Unit
) {
    // Usa la nuova funzione isTaskDisplayOverdue con LocalDate
    val isDisplayOverdue = isTaskDisplayOverdue(task.dueDate)
    val contentAlpha = if (task.isCompleted) 0.5f else 1f
    val textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null

    val taskNameColor = if (isDisplayOverdue && !task.isCompleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    // Rinominato dateColor in dateAndMetaColor per più chiarezza
    val dateAndMetaColor = if (isDisplayOverdue && !task.isCompleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .alpha(contentAlpha),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = taskNameColor,
                textDecoration = textDecoration,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Riga per Data e Ora
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mostra icona di scadenza solo se non completato
                if (!task.isCompleted) {
                    Icon(
                        imageVector = if (isDisplayOverdue) Icons.Filled.ErrorOutline else Icons.Filled.Event,
                        contentDescription = if (isDisplayOverdue) "Task Overdue" else "Due Date",
                        tint = dateAndMetaColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    // Usa il nuovo formatter per LocalDate
                    text = "Due: ${formatDisplayDate(task.dueDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = dateAndMetaColor,
                    textDecoration = textDecoration
                )
                // Visualizza Ora Inizio/Fine
                Text(
                    // Usa il nuovo formatter per LocalTime
                    text = " (${formatDisplayTime(task.startTime)}${task.endTime?.let { " - ${formatDisplayTime(it)}" } ?: ""})",
                    style = MaterialTheme.typography.bodySmall,
                    color = dateAndMetaColor,
                    textDecoration = textDecoration,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Visualizzazione Ricorrenza se non è "Once"
            if (task.recurrence != RecurrenceType.ONCE) { // Assicurati che RecurrenceType sia importato
                Text(
                    text = "Recurs: ${task.recurrence.displayName}", // Usa displayName dall'enum
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
                    color = dateAndMetaColor.copy(alpha = 0.8f),
                    textDecoration = textDecoration,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onToggleCompletion,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = if (task.isCompleted) "Mark as incomplete" else "Mark as complete",
                tint = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CurrentTasksCard(
    tasks: List<Task>,
    onToggleTaskCompletion: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your Current Tasks",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks scheduled. Great job or time to add some!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(tasks, key = { task -> task.id }) { task ->
                        TaskRow(
                            task = task,
                            onToggleCompletion = { onToggleTaskCompletion(task.id) }
                        )
                        if (tasks.last() != task) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}



// --- PREVIEWS ---
private fun getPreviewLocalDateForDaysOffset(days: Int): LocalDate {
    return LocalDate.now().plusDays(days.toLong())
}
private fun getPreviewLocalTime(hour: Int, minute: Int): LocalTime {
    return LocalTime.of(hour, minute)
}

// --- PREVIEWS ---

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun HomeScreenPreview() {
    XianZaiTheme {
        val sampleTasksForPreview = listOf(
            Task(
                name = "Task due today, incomplete",
                dueDate = getPreviewLocalDateForDaysOffset(0),
                startTime = getPreviewLocalTime(9, 0),
                endTime = getPreviewLocalTime(10, 0),
                recurrence = RecurrenceType.DAILY,
                isCompleted = false
            ),
            Task(
                name = "Task overdue, incomplete",
                dueDate = getPreviewLocalDateForDaysOffset(-1),
                startTime = getPreviewLocalTime(14, 0),
                endTime = null, // Senza ora di fine
                recurrence = RecurrenceType.ONCE,
                isCompleted = false
            ),
            Task(
                name = "Task due tomorrow, completed",
                dueDate = getPreviewLocalDateForDaysOffset(1),
                startTime = getPreviewLocalTime(11, 0),
                endTime = getPreviewLocalTime(11, 30),
                recurrence = RecurrenceType.ONCE,
                isCompleted = true
            ),
            Task( // Task "no due date" ora richiede una data, quindi mettiamo una data futura
                name = "Task with future date, incomplete",
                dueDate = getPreviewLocalDateForDaysOffset(5), // Esempio: data futura
                startTime = getPreviewLocalTime(10, 0),
                endTime = null,
                recurrence = RecurrenceType.WEEKLY,
                isCompleted = false
            ),
            Task(
                name = "Task was overdue, now completed",
                dueDate = getPreviewLocalDateForDaysOffset(-2),
                startTime = getPreviewLocalTime(8, 0),
                endTime = getPreviewLocalTime(8, 30),
                recurrence = RecurrenceType.ONCE,
                isCompleted = true
            )
        ).sortedWith(compareBy<Task>{it.isCompleted}.thenBy {it.dueDate}.thenBy {it.startTime}) // Ordinamento come nel ViewModel


        Scaffold(
            topBar = { TopAppBar(title = { Text("Home") }) },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Filled.Home, "Home")}, label = { Text("Home") })
                    NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Filled.CalendarMonth, "Calendar")}, label = { Text("Calendar") })
                    NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Filled.AccountCircle, "Profile")}, label = { Text("Profile") })
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)){ Text("Add Task")}
                    Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)){ Text("Add Event")}
                }
                EnergyCard(chronotype = DEFAULT_CHRONOTYPE)
                CurrentTasksCard(tasks = sampleTasksForPreview, onToggleTaskCompletion = { /* No-op in preview */ })
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 340, name = "Energy Card Intermediate")
@Composable
fun EnergyCardIntermediatePreview() {
    XianZaiTheme {
        EnergyCard(chronotype = "Intermediate")
    }
}

@Preview(showBackground = true, widthDp = 340, name = "Energy Card Morning")
@Composable
fun EnergyCardMorningPreview() {
    XianZaiTheme {
        EnergyCard(chronotype = "Definite Morning")
    }
}

@Preview(showBackground = true, widthDp = 340, name = "Energy Card Evening")
@Composable
fun EnergyCardEveningPreview() {
    XianZaiTheme {
        EnergyCard(chronotype = "Definite Evening")
    }
}

@Preview(showBackground = true, widthDp = 340, name = "Current Tasks Card - Mixed (Sorted)")
@Composable
fun CurrentTasksCardPreview_Mixed() {
    XianZaiTheme {
        val sampleTasks = listOf(
            Task(
                id = "1", name = "Grocery shopping",
                dueDate = getPreviewLocalDateForDaysOffset(0),
                startTime = getPreviewLocalTime(17, 0),
                endTime = null,
                recurrence = RecurrenceType.ONCE,
                isCompleted = false
            ),
            Task(
                id = "2", name = "Call Mom (Completed)",
                dueDate = getPreviewLocalDateForDaysOffset(-2),
                startTime = getPreviewLocalTime(10, 0),
                endTime = getPreviewLocalTime(10, 15),
                recurrence = RecurrenceType.ONCE,
                isCompleted = true
            ),
            Task(
                id = "3", name = "Study Compose (Overdue)",
                dueDate = getPreviewLocalDateForDaysOffset(-1),
                startTime = getPreviewLocalTime(19, 0),
                endTime = getPreviewLocalTime(20, 30),
                recurrence = RecurrenceType.DAILY,
                isCompleted = false
            ),
            Task( // Task "no due date" ora richiede una data, quindi mettiamo una data futura
                id = "4", name = "Pay bill (Future date)",
                dueDate = getPreviewLocalDateForDaysOffset(7), // Esempio: data futura
                startTime = getPreviewLocalTime(12,0),
                endTime = null,
                recurrence = RecurrenceType.WEEKLY,
                isCompleted = false
            )
        ).sortedWith(compareBy<Task>{it.isCompleted}.thenBy {it.dueDate}.thenBy {it.startTime})
        CurrentTasksCard(tasks = sampleTasks, onToggleTaskCompletion = {})
    }
}

@Preview(showBackground = true, widthDp = 340, name = "Current Tasks Card - Empty")
@Composable
fun CurrentTasksCardPreview_Empty() {
    XianZaiTheme {
        CurrentTasksCard(tasks = emptyList(), onToggleTaskCompletion = {})
    }
}

@Preview(showBackground = true, widthDp = 360, name = "Task Row - Incomplete, Not Overdue")
@Composable
fun TaskRowPreview_IncompleteNotOverdue() {
    XianZaiTheme {
        Surface(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            TaskRow(
                task = Task(
                    name = "Sample Task, not overdue, very long name to test wrapping",
                    dueDate = getPreviewLocalDateForDaysOffset(2),
                    startTime = getPreviewLocalTime(9, 0),
                    endTime = getPreviewLocalTime(9, 45),
                    recurrence = RecurrenceType.ONCE,
                    isCompleted = false
                ),
                onToggleCompletion = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "Task Row - Incomplete, Overdue")
@Composable
fun TaskRowPreview_IncompleteOverdue() {
    XianZaiTheme {
        Surface(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            TaskRow(
                task = Task(
                    name = "Sample Task, OVERDUE",
                    dueDate = getPreviewLocalDateForDaysOffset(-1),
                    startTime = getPreviewLocalTime(10, 0),
                    endTime = null,
                    recurrence = RecurrenceType.ONCE,
                    isCompleted = false
                ),
                onToggleCompletion = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "Task Row - Completed, Not Overdue")
@Composable
fun TaskRowPreview_Completed() {
    XianZaiTheme {
        Surface(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            TaskRow(
                task = Task(
                    name = "Sample Task, completed",
                    dueDate = getPreviewLocalDateForDaysOffset(2),
                    startTime = getPreviewLocalTime(14, 0),
                    endTime = getPreviewLocalTime(15, 0),
                    recurrence = RecurrenceType.ONCE,
                    isCompleted = true
                ),
                onToggleCompletion = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "Task Row - Completed, Was Overdue (Visual)")
@Composable
fun TaskRowPreview_CompletedWasOverdueVisual() {
    XianZaiTheme {
        Surface(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            TaskRow(
                task = Task(
                    name = "Sample Task, completed (was overdue)",
                    dueDate = getPreviewLocalDateForDaysOffset(-5),
                    startTime = getPreviewLocalTime(8, 0),
                    endTime = getPreviewLocalTime(8, 30),
                    recurrence = RecurrenceType.ONCE,
                    isCompleted = true
                ),
                onToggleCompletion = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "Task Row - Daily Recurrence, Incomplete")
@Composable
fun TaskRowPreview_DailyRecurrenceIncomplete() { // Rinominata per chiarezza
    XianZaiTheme {
        Surface(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            TaskRow(
                task = Task(
                    name = "Daily sync meeting",
                    dueDate = getPreviewLocalDateForDaysOffset(0), // Oggi
                    startTime = getPreviewLocalTime(9, 0),
                    endTime = getPreviewLocalTime(9, 15),
                    recurrence = RecurrenceType.DAILY,
                    isCompleted = false
                ),
                onToggleCompletion = {}
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, name = "Task Row - Weekly Recurrence, Completed")
@Composable
fun TaskRowPreview_WeeklyRecurrenceCompleted() { // Rinominata per chiarezza
    XianZaiTheme {
        Surface(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            TaskRow(
                task = Task(
                    name = "Weekly review session",
                    dueDate = getPreviewLocalDateForDaysOffset(15), // Tra 15 giorni
                    startTime = getPreviewLocalTime(15, 0),
                    endTime = getPreviewLocalTime(16, 0),
                    recurrence = RecurrenceType.WEEKLY,
                    isCompleted = true
                ),
                onToggleCompletion = {}
            )
        }
    }
}