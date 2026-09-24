package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DownloadedItemEntity::class], version = 1, exportSchema = false)
abstract class KhatifDatabase : RoomDatabase() {
  abstract fun downloadedItemDao(): DownloadedItemDao

  companion object {
    @Volatile
    private var INSTANCE: KhatifDatabase? = null

    fun getDatabase(context: Context): KhatifDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          KhatifDatabase::class.java,
          "khatif_downloads_v1.db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
