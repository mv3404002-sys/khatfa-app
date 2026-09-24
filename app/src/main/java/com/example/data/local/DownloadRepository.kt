package com.example.data.local

import com.example.model.DownloadedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DownloadRepository(private val dao: DownloadedItemDao) {
  val allDownloads: Flow<List<DownloadedItem>> = dao.getAllItems().map { list ->
    list.map { it.toModel() }
  }

  suspend fun insert(item: DownloadedItem) {
    dao.insertItem(DownloadedItemEntity.fromModel(item))
  }

  suspend fun updateExportStatus(id: String, isExported: Boolean, contentUri: String?) {
    dao.updateGalleryExportStatus(id, isExported, contentUri)
  }

  suspend fun deleteById(id: String) {
    dao.deleteById(id)
  }

  suspend fun renameItem(id: String, newTitle: String) {
    dao.updateTitle(id, newTitle)
  }

  suspend fun deleteAll() {
    dao.deleteAll()
  }

  suspend fun getAllSync(): List<DownloadedItem> {
    return dao.getAllItemsSync().map { it.toModel() }
  }

  suspend fun getAllEntitiesSync(): List<DownloadedItemEntity> {
    return dao.getAllItemsSync()
  }
}
