package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VideoProject
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoProjectDao {
    @Query("SELECT * FROM video_projects ORDER BY updatedAtMillis DESC")
    fun getAllProjects(): Flow<List<VideoProject>>

    @Query("SELECT * FROM video_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): VideoProject?

    @Query("SELECT * FROM video_projects WHERE status = :status ORDER BY updatedAtMillis DESC")
    fun getProjectsByStatus(status: String): Flow<List<VideoProject>>

    @Query("SELECT * FROM video_projects WHERE format = :format ORDER BY updatedAtMillis DESC")
    fun getProjectsByFormat(format: String): Flow<List<VideoProject>>

    @Query("SELECT * FROM video_projects WHERE scheduledDateMillis IS NOT NULL ORDER BY scheduledDateMillis ASC")
    fun getScheduledProjects(): Flow<List<VideoProject>>

    @Query("SELECT * FROM video_projects WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchProjects(query: String): Flow<List<VideoProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: VideoProject): Long

    @Update
    suspend fun updateProject(project: VideoProject)

    @Delete
    suspend fun deleteProject(project: VideoProject)

    @Query("DELETE FROM video_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("SELECT COUNT(*) FROM video_projects")
    suspend fun getCount(): Int
}
