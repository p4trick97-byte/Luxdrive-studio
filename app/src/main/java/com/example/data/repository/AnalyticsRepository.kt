package com.example.data.repository

import com.example.data.local.ChannelMetricDao
import com.example.data.model.ChannelMetric
import com.example.data.model.MonetizationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AnalyticsRepository(private val dao: ChannelMetricDao) {

    val allMetrics: Flow<List<ChannelMetric>> = dao.getAllMetrics()
    val latestMetric: Flow<ChannelMetric?> = dao.getLatestMetric()
    val recentMetricsHistory: Flow<List<ChannelMetric>> = dao.getRecentMetricsHistory()

    val monetizationStatus: Flow<MonetizationStatus> = dao.getLatestMetric().map { latest ->
        if (latest != null) {
            MonetizationStatus(
                currentSubscribers = latest.totalSubscribers,
                currentWatchHours = latest.totalWatchHours,
                currentShortsViews = latest.shortsViews,
                isMonetizedConfirmed = latest.totalSubscribers >= 1000 && (latest.totalWatchHours >= 4000.0 || latest.shortsViews >= 10_000_000L),
                estimatedDaysToThreshold = calculateDaysRemaining(latest.totalSubscribers, latest.totalWatchHours)
            )
        } else {
            MonetizationStatus()
        }
    }

    suspend fun recordMetric(metric: ChannelMetric): Long {
        return dao.insertMetric(metric)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }

    private fun calculateDaysRemaining(subs: Int, hours: Double): Int {
        val missingSubs = (1000 - subs).coerceAtLeast(0)
        val missingHours = (4000.0 - hours).coerceAtLeast(0.0)
        val daysBySubs = (missingSubs / 8.0).toInt().coerceAtLeast(1)
        val daysByHours = (missingHours / 25.0).toInt().coerceAtLeast(1)
        return maxOf(daysBySubs, daysByHours)
    }

    suspend fun seedInitialMetricsIfEmpty() {
        if (dao.getCount() == 0) {
            val now = System.currentTimeMillis()
            val day = 86_400_000L

            // 7 data points representing the channel ramp-up
            val points = listOf(
                ChannelMetric(recordDateMillis = now - (28 * day), totalSubscribers = 45, totalWatchHours = 120.0, totalViews = 3800, shortsViews = 4500, impressions = 32000, ctrPercent = 5.2, avgViewDurationSecs = 190, retentionRatePercent = 38.0, isDemoData = true, dataSource = "DEMO_SEED", notes = "Primi video pubblicati"),
                ChannelMetric(recordDateMillis = now - (21 * day), totalSubscribers = 95, totalWatchHours = 280.0, totalViews = 9200, shortsViews = 12000, impressions = 78000, ctrPercent = 6.4, avgViewDurationSecs = 220, retentionRatePercent = 42.0, isDemoData = true, dataSource = "DEMO_SEED", notes = "Uscita video Ferrari 488"),
                ChannelMetric(recordDateMillis = now - (14 * day), totalSubscribers = 180, totalWatchHours = 560.0, totalViews = 18500, shortsViews = 24000, impressions = 145000, ctrPercent = 7.1, avgViewDurationSecs = 245, retentionRatePercent = 46.5, isDemoData = true, dataSource = "DEMO_SEED", notes = "Crescita Shorts virali"),
                ChannelMetric(recordDateMillis = now - (7 * day), totalSubscribers = 275, totalWatchHours = 910.0, totalViews = 31000, shortsViews = 38000, impressions = 230000, ctrPercent = 8.0, avgViewDurationSecs = 270, retentionRatePercent = 49.0, isDemoData = true, dataSource = "DEMO_SEED", notes = "Picco retention video lunghi"),
                ChannelMetric(recordDateMillis = now, totalSubscribers = 368, totalWatchHours = 1290.0, totalViews = 46500, shortsViews = 52000, subscribersGained = 42, subscribersLost = 3, impressions = 320000, ctrPercent = 8.4, avgViewDurationSecs = 295, retentionRatePercent = 51.2, estimatedRevenueEur = 0.0, rpmEur = 0.0, cpmEur = 0.0, isDemoData = true, dataSource = "DEMO_SEED", notes = "Rilevazione dimostrativa LuxDrive v1.0")
            )

            for (p in points) {
                dao.insertMetric(p)
            }
        }
    }
}
