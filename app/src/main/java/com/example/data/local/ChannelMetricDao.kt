package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ChannelMetric
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelMetricDao {
    @Query("SELECT * FROM channel_metrics ORDER BY recordDateMillis DESC")
    fun getAllMetrics(): Flow<List<ChannelMetric>>

    @Query("SELECT * FROM channel_metrics ORDER BY recordDateMillis DESC LIMIT 1")
    fun getLatestMetric(): Flow<ChannelMetric?>

    @Query("SELECT * FROM channel_metrics ORDER BY recordDateMillis DESC LIMIT 30")
    fun getRecentMetricsHistory(): Flow<List<ChannelMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: ChannelMetric): Long

    @Query("DELETE FROM channel_metrics")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM channel_metrics")
    suspend fun getCount(): Int
}
