package com.thierryguichardaz.xianzai.ui.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thierryguichardaz.xianzai.data.model.RecurrenceType
import com.thierryguichardaz.xianzai.data.model.Task
import com.thierryguichardaz.xianzai.data.preferences.DEFAULT_CHRONOTYPE
import com.thierryguichardaz.xianzai.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

private fun getLocalDateForDaysOffset(days: Int): LocalDate {
    return LocalDate.now().plusDays(days.toLong())
}

class HomeViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userChronotype: StateFlow<String> = userPreferencesRepository.userChronotype
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DEFAULT_CHRONOTYPE
        )

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _showAddTaskDialog = MutableStateFlow(false)
    val showAddTaskDialog: StateFlow<Boolean> = _showAddTaskDialog.asStateFlow()

    // Stati per i campi del form
    val taskFormName = MutableStateFlow("")
    val taskFormDueDate = MutableStateFlow(LocalDate.now())
    val taskFormStartTime = MutableStateFlow(LocalTime.of(8, 0))
    val taskFormEndTime = MutableStateFlow<LocalTime?>(null)
    val taskFormRecurrence = MutableStateFlow(RecurrenceType.ONCE)
    val taskFormPriority = MutableStateFlow(3) // NUOVO: per la priorità, default 3
    val taskFormCognitiveLoad = MutableStateFlow(3) // NUOVO: per lo sforzo, default 3

    fun openAddTaskDialog() {
        taskFormName.value = ""
        taskFormDueDate.value = LocalDate.now()
        taskFormStartTime.value = LocalTime.of(8, 0)
        taskFormEndTime.value = null
        taskFormRecurrence.value = RecurrenceType.ONCE
        taskFormPriority.value = 3 // IMPOSTA DEFAULT
        taskFormCognitiveLoad.value = 3 // IMPOSTA DEFAULT
        _showAddTaskDialog.value = true
    }

    fun dismissAddTaskDialog() {
        _showAddTaskDialog.value = false
    }

    init {
        loadDummyTasks()
    }

    private fun loadDummyTasks() {
        _tasks.value = sortTasks(
            listOf(
                Task(
                    name = "Team Meeting Presentation",
                    dueDate = getLocalDateForDaysOffset(2),
                    startTime = LocalTime.of(10,0),
                    endTime = LocalTime.of(11,30),
                    recurrence = RecurrenceType.ONCE,
                    isCompleted = false,
                    priority = 4, // Esempio
                    cognitiveLoad = 5  // Esempio
                ),
                Task(
                    name = "Code Review",
                    dueDate = getLocalDateForDaysOffset(-1),
                    startTime = LocalTime.of(14,0),
                    endTime = LocalTime.of(15,0),
                    recurrence = RecurrenceType.ONCE,
                    isCompleted = true,
                    priority = 2,
                    cognitiveLoad = 4
                ),
                Task(
                    name = "Daily Stand-up",
                    dueDate = getLocalDateForDaysOffset(0),
                    startTime = LocalTime.of(9,0),
                    endTime = LocalTime.of(9,15),
                    recurrence = RecurrenceType.DAILY,
                    isCompleted = false
                    // priority e cognitiveLoad useranno il default 3 da Task data class
                ),
                Task(
                    name = "Weekly Status Report",
                    dueDate = LocalDate.now().plusDays( (java.time.DayOfWeek.FRIDAY.value - LocalDate.now().dayOfWeek.value + 7) % 7L ),
                    startTime = LocalTime.of(16,0),
                    endTime = null,
                    recurrence = RecurrenceType.WEEKLY,
                    isCompleted = false,
                    priority = 3,
                    cognitiveLoad = 2
                ),
            )
        )
    }

    private fun sortTasks(tasks: List<Task>): List<Task> {
        return tasks.sortedWith(
            compareBy<Task> { it.isCompleted }
                .thenByDescending { it.priority } // Ordina per priorità (5 più alta viene prima)
                .thenBy { it.dueDate }
                .thenBy { it.startTime }
        )
    }

    fun saveNewTask() {
        val name = taskFormName.value.trim()
        if (name.isBlank()) {
            // Potresti voler mostrare un messaggio all'utente qui
            return
        }

        val dueDate = taskFormDueDate.value
        val startTime = taskFormStartTime.value
        var endTime = taskFormEndTime.value

        if (endTime == null) {
            endTime = startTime.plusMinutes(30)
        } else if (endTime.isBefore(startTime) || endTime == startTime) {
            // Potresti voler mostrare un messaggio o gestire questo caso diversamente
            endTime = startTime.plusMinutes(30) // Semplice fallback
        }

        val newTask = Task(
            name = name,
            dueDate = dueDate,
            startTime = startTime,
            endTime = endTime,
            recurrence = taskFormRecurrence.value,
            isCompleted = false,
            priority = taskFormPriority.value, // Usa il valore dal form
            cognitiveLoad = taskFormCognitiveLoad.value // Usa il valore dal form
        )

        _tasks.update { currentTasks ->
            sortTasks(currentTasks + newTask)
        }
        dismissAddTaskDialog()
    }

    private fun isTaskOverdue(task: Task): Boolean {
        return task.dueDate.isBefore(LocalDate.now(ZoneId.systemDefault()))
    }

    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            _tasks.update { currentTasks ->
                val taskToToggle = currentTasks.find { it.id == taskId }
                if (taskToToggle == null) {
                    // Task non trovato, non fare nulla o logga un errore
                    return@update currentTasks
                }

                val isNowCompleted = !taskToToggle.isCompleted // Stato di completamento che il task AVRÀ

                if (isNowCompleted && isTaskOverdue(taskToToggle)) {
                    // CASO: Il task viene segnato come COMPLETO ED è SCADUTO -> Rimuovilo
                    sortTasks(currentTasks.filterNot { it.id == taskId })
                } else {
                    // CASO: Il task viene segnato come incompleto, OPPURE
                    // viene segnato come completo MA NON è scaduto -> Aggiorna solo lo stato
                    val updatedTasks = currentTasks.map {
                        if (it.id == taskId) {
                            it.copy(isCompleted = isNowCompleted)
                        } else {
                            it
                        }
                    }
                    sortTasks(updatedTasks)
                }
            }
        }
    }
}
