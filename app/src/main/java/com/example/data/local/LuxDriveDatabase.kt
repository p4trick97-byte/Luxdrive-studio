package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ChannelMetric
import com.example.data.model.VideoProject

@Database(
    entities = [VideoProject::class, ChannelMetric::class],
    version = 2,
    exportSchema = false
)
abstract class LuxDriveDatabase : RoomDatabase() {
    abstract fun videoProjectDao(): VideoProjectDao
    abstract fun channelMetricDao(): ChannelMetricDao

    companion object {
        @Volatile
        private var INSTANCE: LuxDriveDatabase? = null

        fun getDatabase(context: Context): LuxDriveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuxDriveDatabase::class.java,
                    "luxdrive_studio.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
