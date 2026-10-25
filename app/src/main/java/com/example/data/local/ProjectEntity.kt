package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creator_projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val niche: String = "Tech & AI",
    val format: String = "Long-form (8-15 min)",
    val status: String = "Idea", // Idea, Scripting, Recording, Editing, Ready, Published
    val targetAudience: String = "Creators & tech enthusiasts looking to scale their production",
    val hook: String = "",
    val outline: String = "",
    val fullScript: String = "",
    val tone: String = "Energetic & Engaging",
    val sceneNotes: String = "",
    val thumbnailPrompt: String = "",
    val thumbnailImageUrl: String? = null,
    val thumbnailResolution: String = "2K",
    val generatedVideoStatus: String = "None", // None, Generating, Ready
    val generatedVideoUrl: String? = null,
    val scheduledDate: String = "", // YYYY-MM-DD
    val retentionScore: Int = 85,
    val retentionTips: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
