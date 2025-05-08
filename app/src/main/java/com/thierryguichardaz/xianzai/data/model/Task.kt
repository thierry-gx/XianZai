package com.thierryguichardaz.xianzai.data.model // o xianzaiv2...

// import androidx.room.Entity // RIMUOVI
// import androidx.room.PrimaryKey // RIMUOVI
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

enum class RecurrenceType (val displayName: String) {
    ONCE("Once"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
}

// @Entity(tableName = "tasks") // RIMUOVI ANNOTAZIONE
data class Task(
    // @PrimaryKey // RIMUOVI ANNOTAZIONE
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dueDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime?,
    val recurrence: RecurrenceType,
    var isCompleted: Boolean = false,
    val priority: Int = 3, // MANTIENI
    val cognitiveLoad: Int = 3 // MANTIENI
)
