package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedItemDao {
  @Query("SELECT * FROM downloaded_items ORDER BY timestamp DESC")
  fun getAllItems(): Flow<List<DownloadedItemEntity>>

  @Query("SELECT * FROM downloaded_items ORDER BY timestamp DESC")
  suspend fun getAllItemsSync(): List<DownloadedItemEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertItem(item: DownloadedItemEntity)

  @Update
  suspend fun updateItem(item: DownloadedItemEntity)

  @Query("DELETE FROM downloaded_items WHERE id = :id")
  suspend fun deleteById(id: String)

  @Query("DELETE FROM downloaded_items")
  suspend fun deleteAll()

  @Query("UPDATE downloaded_items SET title = :newTitle WHERE id = :id")
  suspend fun updateTitle(id: String, newTitle: String)

  @Query("UPDATE downloaded_items SET isExportedToGallery = :isExported, contentUri = :contentUri WHERE id = :id")
  suspend fun updateGalleryExportStatus(id: String, isExported: Boolean, contentUri: String?)
}
