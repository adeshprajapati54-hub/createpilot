package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "calendar_entries",
    indices = [Index(value = ["scheduledDate"]), Index(value = ["projectId"])]
)
data class CalendarEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long? = null,
    val title: String,
    val scheduledDate: String, // YYYY-MM-DD
    val status: String = "Idea",
    val niche: String = "Tech & AI",
    val format: String = "Long-form (8-15 min)",
    val productionNotes: String = "",
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
