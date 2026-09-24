package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.DownloadedItem
import java.io.File

object MediaShareUtil {
  fun shareMediaItem(
    context: Context,
    item: DownloadedItem,
    language: AppLanguage,
    onError: (String) -> Unit = {}
  ) {
    val mime = item.mimeType ?: if (item.isAudioOnly) "audio/*" else "video/*"
    var shareUri: Uri? = null

    if (!item.contentUri.isNullOrBlank()) {
      try {
        shareUri = Uri.parse(item.contentUri)
      } catch (_: Exception) {}
    }

    if (shareUri == null && !item.localFilePath.isNullOrBlank()) {
      val file = File(item.localFilePath)
      if (file.exists() && file.length() > 0) {
        try {
          shareUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
          )
        } catch (_: Exception) {
          shareUri = Uri.fromFile(file)
        }
      }
    }

    if (shareUri == null) {
      onError(AppStrings.mediaFileNotFound(language))
      return
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
      type = mime
      putExtra(Intent.EXTRA_STREAM, shareUri)
      putExtra(Intent.EXTRA_SUBJECT, item.title)
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    try {
      val chooser = Intent.createChooser(shareIntent, item.title).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(chooser)
    } catch (_: Exception) {
      onError(AppStrings.mediaFileNotFound(language))
    }
  }
}
