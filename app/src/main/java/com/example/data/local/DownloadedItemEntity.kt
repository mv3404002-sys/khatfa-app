package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.DownloadedItem
import com.example.model.PlatformType

@Entity(tableName = "downloaded_items")
data class DownloadedItemEntity(
  @PrimaryKey
  val id: String,
  val title: String,
  val author: String,
  val duration: String,
  val size: String,
  val quality: String,
  val platformName: String,
  val date: String,
  val isAudioOnly: Boolean,
  val thumbnailUrl: String?,
  val localFilePath: String?,
  val contentUri: String?,
  val mimeType: String?,
  val isExportedToGallery: Boolean,
  val timestamp: Long = System.currentTimeMillis()
) {
  fun toModel(): DownloadedItem {
    val platform = try {
      PlatformType.valueOf(platformName)
    } catch (_: Exception) {
      PlatformType.YOUTUBE
    }
    return DownloadedItem(
      id = id,
      title = title,
      author = author,
      duration = duration,
      size = size,
      quality = quality,
      platform = platform,
      date = date,
      isAudioOnly = isAudioOnly,
      thumbnailUrl = thumbnailUrl,
      localFilePath = localFilePath,
      contentUri = contentUri,
      mimeType = mimeType,
      isExportedToGallery = isExportedToGallery
    )
  }

  companion object {
    fun fromModel(item: DownloadedItem, timestamp: Long = System.currentTimeMillis()): DownloadedItemEntity {
      return DownloadedItemEntity(
        id = item.id,
        title = item.title,
        author = item.author,
        duration = item.duration,
        size = item.size,
        quality = item.quality,
        platformName = item.platform.name,
        date = item.date,
        isAudioOnly = item.isAudioOnly,
        thumbnailUrl = item.thumbnailUrl,
        localFilePath = item.localFilePath,
        contentUri = item.contentUri,
        mimeType = item.mimeType,
        isExportedToGallery = item.isExportedToGallery,
        timestamp = timestamp
      )
    }
  }
}
