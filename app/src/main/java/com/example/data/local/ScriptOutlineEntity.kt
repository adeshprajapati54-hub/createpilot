package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "script_outlines",
    indices = [Index(value = ["projectId"])]
)
data class ScriptOutlineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long = 0,
    val videoTitle: String = "",
    val hookText: String = "",
    val outlineText: String = "",
    val fullNarration: String = "",
    val tone: String = "Energetic & Engaging",
    val wordCount: Int = 0,
    val estimatedDurationMinutes: Double = 0.0,
    val isAiGenerated: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
