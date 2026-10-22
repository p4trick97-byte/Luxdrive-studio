package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel_metrics")
data class ChannelMetric(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordDateMillis: Long = System.currentTimeMillis(),
    val totalSubscribers: Int = 0,
    val totalWatchHours: Double = 0.0,
    val totalViews: Long = 0,
    val shortsViews: Long = 0,
    val subscribersGained: Int = 0,
    val subscribersLost: Int = 0,
    val impressions: Long = 0,
    val ctrPercent: Double = 0.0,
    val avgViewDurationSecs: Int = 0,
    val retentionRatePercent: Double = 0.0,
    val estimatedRevenueEur: Double = 0.0,
    val rpmEur: Double = 0.0,
    val cpmEur: Double = 0.0,
    val isDemoData: Boolean = true,
    val dataSource: String = "DEMO_SEED", // "DEMO_SEED", "MANUAL_ENTRY", "YOUTUBE_API_OFFICIAL"
    val notes: String = ""
)

data class MonetizationStatus(
    val currentSubscribers: Int = 340,
    val requiredSubscribers: Int = 1000,
    val currentWatchHours: Double = 1250.0,
    val requiredWatchHours: Double = 4000.0,
    val currentShortsViews: Long = 45000L,
    val requiredShortsViews: Long = 10_000_000L,
    val isMonetizedConfirmed: Boolean = false,
    val estimatedDaysToThreshold: Int = 85
) {
    val subscribersProgressPercent: Float
        get() = (currentSubscribers.toFloat() / requiredSubscribers).coerceIn(0f, 1f)

    val watchHoursProgressPercent: Float
        get() = (currentWatchHours.toFloat() / requiredWatchHours.toFloat()).coerceIn(0f, 1f)

    val shortsProgressPercent: Float
        get() = (currentShortsViews.toFloat() / requiredShortsViews.toFloat()).coerceIn(0f, 1f)
}
