package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProjectOrigin(val displayName: String, val badgeLabel: String) {
    DEMO_SEED("Dimostrativo", "Demo"),
    USER_CREATED("Manuale", "Bozza Manuale"),
    AI_GENERATED("Gemini AI", "Generato con AI")
}

@Entity(tableName = "video_projects")
data class VideoProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val format: String = ProjectFormat.LONG_VIDEO.name,
    val status: String = ProjectStatus.DRAFT.name,
    val scheduledDateMillis: Long? = null,
    val publishedDateMillis: Long? = null,
    val youtubeVideoId: String = "",
    val targetAudience: String = "Appassionati di motori italiani, acquirenti di auto sportive e amanti delle storie tecniche",
    val hook: String = "",
    val researchAndSources: String = "",
    val fullScript: String = "",
    val voiceOverNotes: String = "",
    val storyboardJson: String = "",
    val capcutEditingNotes: String = "",
    val estimatedDurationMinutes: Int = 12,
    val youtubeTitleAlternative: String = "",
    val description: String = "",
    val chaptersText: String = "",
    val tags: String = "",
    val thumbnailConcept: String = "",
    val shortsDerivedScript: String = "",
    val tiktokAdaptation: String = "",
    val instagramReelAdaptation: String = "",
    // Real / Recorded Analytics on publication
    val viewsCount: Long = 0,
    val watchHours: Double = 0.0,
    val ctrPercent: Double = 0.0,
    val likesCount: Long = 0,
    val commentsCount: Long = 0,
    val isDemoAnalytics: Boolean = true,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis(),
    // Origin & Generation metadata for transparency and reproducibility
    val origin: String = ProjectOrigin.USER_CREATED.name,
    val carModel: String = "",
    val contentGoal: String = "",
    val narrativeTone: String = "",
    val userNotes: String = ""
)
