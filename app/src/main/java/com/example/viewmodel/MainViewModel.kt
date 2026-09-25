package com.example.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DownloadRepository
import com.example.data.local.KhatifDatabase
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.DownloadedItem
import com.example.model.PlatformType
import com.example.model.VideoPreviewData
import com.example.model.VideoQualityOption
import com.example.network.KhatfaApiClient
import com.example.ui.theme.ThemeMode
import com.example.util.AudioExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.UUID

enum class HistoryFilter(val label: String) {
  ALL("الكل"),
  VIDEO("فيديو"),
  AUDIO("صوت")
}

enum class BackendConnectionStatus {
  CHECKING,
  ONLINE,
  OFFLINE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
  private val _urlInput = MutableStateFlow("")
  val urlInput: StateFlow<String> = _urlInput.asStateFlow()

  private val _isAnalyzing = MutableStateFlow(false)
  val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

  private val _analysisElapsedSeconds = MutableStateFlow(0)
  val analysisElapsedSeconds: StateFlow<Int> = _analysisElapsedSeconds.asStateFlow()
  private var analysisTimerJob: Job? = null

  // In-Memory cache for parsed video links (no re-fetching required)
  private val videoInfoCache = mutableMapOf<String, VideoPreviewData>()

  private val _previewData = MutableStateFlow<VideoPreviewData?>(null)
  val previewData: StateFlow<VideoPreviewData?> = _previewData.asStateFlow()

  private val _isDownloading = MutableStateFlow(false)
  val isDownloading: StateFlow<Boolean> = _isDownloading.asStateFlow()

  private val _downloadProgress = MutableStateFlow(0f)
  val downloadProgress: StateFlow<Float> = _downloadProgress.asStateFlow()

  private val _downloadStatusDetail = MutableStateFlow("")
  val downloadStatusDetail: StateFlow<String> = _downloadStatusDetail.asStateFlow()

  // Event to auto-navigate to History when a download completes
  private val _navigateToHistoryEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
  val navigateToHistoryEvent: SharedFlow<Unit> = _navigateToHistoryEvent.asSharedFlow()

  private val _backendStatus = MutableStateFlow(BackendConnectionStatus.CHECKING)
  val backendStatus: StateFlow<BackendConnectionStatus> = _backendStatus.asStateFlow()

  private val prefs = application.getSharedPreferences("khatif_user_settings", Context.MODE_PRIVATE)
  private val database = KhatifDatabase.getDatabase(application)
  private val downloadRepository = DownloadRepository(database.downloadedItemDao())

  private val _themeMode = MutableStateFlow(
    when (prefs.getString("pref_theme_mode", "SYSTEM")) {
      "DARK" -> ThemeMode.DARK
      "LIGHT" -> ThemeMode.LIGHT
      else -> ThemeMode.SYSTEM
    }
  )
  val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

  private val _language = MutableStateFlow(
    when (prefs.getString("pref_language", "ARABIC")) {
      "ENGLISH" -> AppLanguage.ENGLISH
      "FRENCH" -> AppLanguage.FRENCH
      else -> AppLanguage.ARABIC
    }
  )
  val language: StateFlow<AppLanguage> = _language.asStateFlow()

  // Font scale (default slightly larger for supreme readability: 1.05x)
  private val _fontSizeScale = MutableStateFlow(prefs.getFloat("pref_font_scale", 1.05f))
  val fontSizeScale: StateFlow<Float> = _fontSizeScale.asStateFlow()

  private val _historyFilter = MutableStateFlow(HistoryFilter.ALL)
  val historyFilter: StateFlow<HistoryFilter> = _historyFilter.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  // Storage & Download Settings
  private val _savePath = MutableStateFlow(prefs.getString("pref_save_path", "المساحة الخاصة بالتطبيق (افتراضي)") ?: "المساحة الخاصة بالتطبيق (افتراضي)")
  val savePath: StateFlow<String> = _savePath.asStateFlow()

  private val _defaultQuality = MutableStateFlow(prefs.getString("pref_default_quality", "أفضل جودة (تلقائي / Best)") ?: "أفضل جودة (تلقائي / Best)")
  val defaultQuality: StateFlow<String> = _defaultQuality.asStateFlow()

  private val _cacheSize = MutableStateFlow("0.0 MB")
  val cacheSize: StateFlow<String> = _cacheSize.asStateFlow()

  private val _wifiOnly = MutableStateFlow(prefs.getBoolean("pref_wifi_only", true))
  val wifiOnly: StateFlow<Boolean> = _wifiOnly.asStateFlow()

  private val _notificationsEnabled = MutableStateFlow(prefs.getBoolean("pref_notifications_enabled", true))
  val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

  private val _autoAnalyzeOnPaste = MutableStateFlow(prefs.getBoolean("pref_auto_analyze_paste", true))
  val autoAnalyzeOnPaste: StateFlow<Boolean> = _autoAnalyzeOnPaste.asStateFlow()

  private val _autoExportToGallery = MutableStateFlow(prefs.getBoolean("pref_auto_export_gallery", false))
  val autoExportToGallery: StateFlow<Boolean> = _autoExportToGallery.asStateFlow()

  private val _hapticFeedbackEnabled = MutableStateFlow(prefs.getBoolean("pref_haptic_feedback", true))
  val hapticFeedbackEnabled: StateFlow<Boolean> = _hapticFeedbackEnabled.asStateFlow()

  // Download History Items (persisted via Room SQLite)
  private val _historyList = MutableStateFlow<List<DownloadedItem>>(emptyList())
  val historyList: StateFlow<List<DownloadedItem>> = _historyList.asStateFlow()

  init {
    viewModelScope.launch {
      downloadRepository.allDownloads.collect { list ->
        _historyList.value = list
      }
    }
    autoDeduplicateHistory()
    recoverExistingFilesIfNeeded()
    checkBackendHealth()
    updateRealCacheSize()
  }

  // Auto-deduplicate history on app launch (Requirement 8)
  fun autoDeduplicateHistory() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val allEntities = downloadRepository.getAllEntitiesSync()
        if (allEntities.size <= 1) return@launch

        val toDeleteIds = mutableListOf<String>()
        val keptList = mutableListOf<com.example.data.local.DownloadedItemEntity>()

        for (item in allEntities) {
          val duplicate = keptList.firstOrNull { kept ->
            val samePath = !item.localFilePath.isNullOrBlank() && item.localFilePath == kept.localFilePath
            val sameTitleAndType = item.title.trim().equals(kept.title.trim(), ignoreCase = true) &&
              item.isAudioOnly == kept.isAudioOnly
            val timeDiff = Math.abs(item.timestamp - kept.timestamp)
            val closeTime = timeDiff < 5 * 60 * 1000L // within 5 minutes
            val sameSize = item.size.isNotBlank() && item.size == kept.size

            samePath || (sameTitleAndType && (closeTime || sameSize))
          }

          if (duplicate != null) {
            // Keep the one exported to gallery if any, or the newer one
            if (item.isExportedToGallery && !duplicate.isExportedToGallery) {
              toDeleteIds.add(duplicate.id)
              keptList.remove(duplicate)
              keptList.add(item)
            } else {
              toDeleteIds.add(item.id)
            }
          } else {
            keptList.add(item)
          }
        }

        for (id in toDeleteIds) {
          downloadRepository.deleteById(id)
        }
      } catch (_: Exception) {}
    }
  }

  private fun recoverExistingFilesIfNeeded() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val app = getApplication<Application>()
        val existingDbItems = downloadRepository.getAllSync()
        val knownPaths = existingDbItems.mapNotNull { it.localFilePath }.toSet()
        val knownTitles = existingDbItems.map { it.title.trim().lowercase() }.toSet()

        val mediaDirs = listOf(
          Pair(File(app.filesDir, "videos"), false),
          Pair(File(app.filesDir, "audio"), true)
        )

        for ((dir, isAudio) in mediaDirs) {
          if (dir.exists() && dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
              if (file.isFile && file.length() > 0 && !knownPaths.contains(file.absolutePath)) {
                val cleanName = file.nameWithoutExtension.removePrefix("Khatfa_").take(30)
                if (!knownTitles.contains(cleanName.trim().lowercase())) {
                  val recoveredItem = DownloadedItem(
                    id = UUID.randomUUID().toString(),
                    title = cleanName.ifBlank { if (isAudio) "ملف صوتي" else "مقطع فيديو" },
                    author = "Khatif",
                    duration = "00:00",
                    size = formatBytes(file.length()),
                    quality = if (isAudio) "MP3" else "HD",
                    platform = PlatformType.YOUTUBE,
                    date = "محفوظ",
                    isAudioOnly = isAudio,
                    thumbnailUrl = null,
                    localFilePath = file.absolutePath,
                    contentUri = null,
                    mimeType = if (isAudio) "audio/mp4" else "video/mp4",
                    isExportedToGallery = false
                  )
                  downloadRepository.insert(recoveredItem)
                }
              }
            }
          }
        }
      } catch (_: Exception) {}
    }
  }

  fun updateRealCacheSize() {
    viewModelScope.launch(Dispatchers.IO) {
      val total = calculateDirSize(getApplication<Application>().cacheDir)
      _cacheSize.value = formatBytes(total)
    }
  }

  private fun calculateDirSize(dir: File?): Long {
    if (dir == null || !dir.exists()) return 0L
    var bytes = 0L
    dir.listFiles()?.forEach { file ->
      bytes += if (file.isDirectory) calculateDirSize(file) else file.length()
    }
    return bytes
  }


  fun checkBackendHealth() {
    viewModelScope.launch {
      _backendStatus.value = BackendConnectionStatus.CHECKING
      try {
        val res = withContext(Dispatchers.IO) {
          KhatfaApiClient.api.checkServer()
        }
        if (res.status == "ok") {
          _backendStatus.value = BackendConnectionStatus.ONLINE
        } else {
          _backendStatus.value = BackendConnectionStatus.OFFLINE
        }
      } catch (_: Exception) {
        _backendStatus.value = BackendConnectionStatus.OFFLINE
      }
    }
  }

  fun showClipboardEmptyMessage() {
    _snackbarMessage.value = AppStrings.clipboardEmptyMessage(_language.value)
  }

  fun showPlatformMaintenanceMessage(platform: PlatformType) {
    if (platform.isUnderMaintenance) {
      _snackbarMessage.value = AppStrings.platformMaintenanceMessage(_language.value, platform.getName(_language.value))
    }
  }

  fun onUrlChange(newUrl: String) {
    _urlInput.value = newUrl
  }

  fun clearUrl() {
    _urlInput.value = ""
    _previewData.value = null
  }

  fun pasteUrl(pasted: String) {
    val clean = pasted.trim()
    _urlInput.value = clean
    if (_autoAnalyzeOnPaste.value && clean.isNotBlank()) {
      snatchVideo(clean)
    }
  }

  private fun resolveExtAndMime(rawExt: String?, isAudioOnly: Boolean): Pair<String, String> {
    val clean = rawExt?.lowercase()?.trim() ?: if (isAudioOnly) "m4a" else "mp4"
    return when (clean) {
      "m4a" -> Pair("m4a", "audio/mp4")
      "mp3" -> Pair("mp3", "audio/mpeg")
      "opus" -> Pair("opus", "audio/ogg")
      "ogg" -> Pair("ogg", "audio/ogg")
      "aac" -> Pair("aac", "audio/aac")
      "flac" -> Pair("flac", "audio/flac")
      "wav" -> Pair("wav", "audio/wav")
      "webm" -> if (isAudioOnly) Pair("webm", "audio/webm") else Pair("webm", "video/webm")
      "mp4" -> if (isAudioOnly) Pair("m4a", "audio/mp4") else Pair("mp4", "video/mp4")
      "mkv" -> Pair("mkv", "video/x-matroska")
      else -> if (isAudioOnly) Pair("m4a", "audio/mp4") else Pair("mp4", "video/mp4")
    }
  }

  fun snatchVideo(customUrl: String? = null) {
    val targetUrl = (customUrl ?: _urlInput.value).trim()
    if (targetUrl.isEmpty()) {
      _snackbarMessage.value = AppStrings.pasteValidUrlMessage(_language.value)
      return
    }
    _urlInput.value = targetUrl

    // Platform Maintenance Check: YouTube, Instagram, and Twitter/X are under maintenance
    val isYouTube = targetUrl.contains("youtube.com", ignoreCase = true) || targetUrl.contains("youtu.be", ignoreCase = true)
    val isInstagram = targetUrl.contains("instagram.com", ignoreCase = true) || targetUrl.contains("instagr.am", ignoreCase = true)
    val isTwitter = targetUrl.contains("twitter.com", ignoreCase = true) || targetUrl.contains("x.com", ignoreCase = true)

    if (isYouTube || isInstagram || isTwitter) {
      val platform = when {
        isYouTube -> PlatformType.YOUTUBE
        isInstagram -> PlatformType.INSTAGRAM
        else -> PlatformType.TWITTER
      }
      _isAnalyzing.value = false
      _previewData.value = null
      analysisTimerJob?.cancel()
      _snackbarMessage.value = AppStrings.platformMaintenanceMessage(_language.value, platform.getName(_language.value))
      return
    }

    // 1. Check real cache first
    val cached = videoInfoCache[targetUrl]
    if (cached != null) {
      _previewData.value = cached
      return
    }

    _isAnalyzing.value = true
    _previewData.value = null
    _analysisElapsedSeconds.value = 0

    // Start timer ticker for reassuring animated UX
    analysisTimerJob?.cancel()
    analysisTimerJob = viewModelScope.launch {
      while (_isAnalyzing.value) {
        delay(1000L)
        _analysisElapsedSeconds.value += 1
      }
    }

    viewModelScope.launch {
      try {
        val info = withContext(Dispatchers.IO) {
          KhatfaApiClient.api.getVideoInfo(targetUrl)
        }

        val platformStr = (info.platform ?: "").lowercase()
        val detectedPlatform = when {
          platformStr.contains("tiktok") || targetUrl.contains("tiktok", ignoreCase = true) -> PlatformType.TIKTOK
          platformStr.contains("instagram") || targetUrl.contains("instagram", ignoreCase = true) -> PlatformType.INSTAGRAM
          platformStr.contains("facebook") || targetUrl.contains("facebook", ignoreCase = true) || targetUrl.contains("fb.watch", ignoreCase = true) -> PlatformType.FACEBOOK
          platformStr.contains("twitter") || platformStr.contains("x") || targetUrl.contains("twitter", ignoreCase = true) || targetUrl.contains("x.com", ignoreCase = true) -> PlatformType.TWITTER
          else -> PlatformType.YOUTUBE
        }

        val qualitiesList = mutableListOf<VideoQualityOption>()

        // Option 1: أفضل جودة للهاتف (Best Quality for Phone)
        qualitiesList.add(
          VideoQualityOption(
            id = "best_phone",
            label = AppStrings.bestPhoneQualityLabel(_language.value),
            size = AppStrings.bestPhoneQualityDesc(_language.value),
            isAudioOnly = false,
            badge = "الأعلى",
            ext = "mp4",
            mimeType = "video/mp4"
          )
        )

        // Option 2: جودة HD (عالية الدقة 720p / 1080p)
        qualitiesList.add(
          VideoQualityOption(
            id = "hd_quality",
            label = AppStrings.hdQualityLabel(_language.value),
            size = AppStrings.hdQualityDesc(_language.value),
            isAudioOnly = false,
            badge = "HD",
            ext = "mp4",
            mimeType = "video/mp4"
          )
        )

        // Option 3: تحميل الصوت فقط (HQ Audio)
        qualitiesList.add(
          VideoQualityOption(
            id = "best_audio",
            label = AppStrings.audioOnlyQualityLabel(_language.value),
            size = AppStrings.audioOnlyQualityDesc(_language.value),
            isAudioOnly = true,
            badge = "صوت",
            ext = "m4a",
            mimeType = "audio/mp4"
          )
        )

        val distinctQualities = qualitiesList.distinctBy { it.label }
        val preferredQuality = when {
          _defaultQuality.value.contains("HD", ignoreCase = true) -> distinctQualities.firstOrNull { it.id == "hd_quality" }
          _defaultQuality.value.contains("صوت", ignoreCase = true) || _defaultQuality.value.contains("audio", ignoreCase = true) -> distinctQualities.firstOrNull { it.id == "best_audio" }
          else -> distinctQualities.firstOrNull { it.id == "best_phone" }
        } ?: distinctQualities.first()
        val defaultSelectedId = preferredQuality.id

        val parsedPreview = VideoPreviewData(
          id = UUID.randomUUID().toString(),
          title = info.title ?: "فيديو مستخرج",
          author = info.uploader ?: detectedPlatform.getName(_language.value),
          duration = formatDuration(info.duration),
          platform = detectedPlatform,
          originalUrl = targetUrl,
          availableQualities = distinctQualities,
          selectedQualityId = defaultSelectedId,
          thumbnailUrl = info.thumbnail
        )

        // Save to cache
        videoInfoCache[targetUrl] = parsedPreview
        _previewData.value = parsedPreview
      } catch (e: HttpException) {
        val errorBody = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
        val detail = extractErrorDetail(errorBody)
        _snackbarMessage.value = AppStrings.networkErrorMessage(_language.value, detail)
      } catch (e: IOException) {
        _snackbarMessage.value = AppStrings.networkErrorMessage(_language.value, null)
      } catch (e: Exception) {
        _snackbarMessage.value = e.localizedMessage ?: AppStrings.networkErrorMessage(_language.value, null)
      } finally {
        _isAnalyzing.value = false
        analysisTimerJob?.cancel()
      }
    }
  }

  fun selectQuality(qualityId: String) {
    _previewData.update { current ->
      current?.copy(selectedQualityId = qualityId)
    }
  }

  fun startDownload() {
    val preview = _previewData.value ?: return
    if (_isDownloading.value) return

    val selectedQuality = preview.availableQualities.firstOrNull { it.id == preview.selectedQualityId }
      ?: preview.availableQualities.first()

    _isDownloading.value = true
    _downloadProgress.value = 0f
    _downloadStatusDetail.value = "0%"

    viewModelScope.launch {
      val app = getApplication<Application>()

      val cleanTitle = (preview.title.ifBlank { "media" })
        .replace(Regex("[^a-zA-Z0-9._\\-\\u0600-\\u06FF]"), "_")
        .take(35)
      val isAudio = selectedQuality.isAudioOnly
      val ext = if (isAudio) "m4a" else selectedQuality.ext
      val mimeType = if (isAudio) "audio/mp4" else selectedQuality.mimeType
      val fileName = "Khatfa_${cleanTitle}_${System.currentTimeMillis()}.$ext"

      // Backend formatId mapping to ensure robust downloading across all platforms
      val backendFormatId = when {
        isAudio -> "bestaudio/best"
        selectedQuality.id == "hd_quality" -> "best[height<=1080]/best[height<=720]/best"
        else -> "best"
      }

      // Save strictly to app private internal storage by default
      val privateDir = File(app.filesDir, if (isAudio) "audio" else "videos").apply { if (!exists()) mkdirs() }
      val destFile = File(privateDir, fileName)

      var success = false
      var downloadedBytesCount = 0L

      if (isAudio) {
        // Safe, lossless audio download with track extraction
        val tempRawFile = File(privateDir, "temp_stream_${System.currentTimeMillis()}.tmp")
        try {
          val rawOutStream = FileOutputStream(tempRawFile)
          val downloadResult = withContext(Dispatchers.IO) {
            KhatfaApiClient.downloadToStream(
              videoUrl = preview.originalUrl,
              formatId = backendFormatId,
              outputStream = rawOutStream
            ) { progress, readBytes, totalBytes ->
              _downloadProgress.value = progress
              val percent = (progress * 100).toInt()
              val downloadedMb = formatBytes(readBytes)
              _downloadStatusDetail.value = AppStrings.downloadProgressText(_language.value, percent, downloadedMb)
            }
          }
          val streamSuccess = downloadResult.first
          if (streamSuccess && tempRawFile.exists() && tempRawFile.length() > 0L) {
            val extracted = AudioExtractor.extractAudioOrCopy(tempRawFile, destFile)
            if (extracted && destFile.exists() && destFile.length() > 0L) {
              success = true
              downloadedBytesCount = destFile.length()
            }
          }
        } catch (e: Exception) {
          success = false
        } finally {
          if (tempRawFile.exists()) tempRawFile.delete()
        }
      } else {
        // Direct video download
        try {
          val fileOutStream = FileOutputStream(destFile)
          val downloadResult = withContext(Dispatchers.IO) {
            KhatfaApiClient.downloadToStream(
              videoUrl = preview.originalUrl,
              formatId = backendFormatId,
              outputStream = fileOutStream
            ) { progress, readBytes, totalBytes ->
              _downloadProgress.value = progress
              val percent = (progress * 100).toInt()
              val downloadedMb = formatBytes(readBytes)
              _downloadStatusDetail.value = AppStrings.downloadProgressText(_language.value, percent, downloadedMb)
            }
          }
          success = downloadResult.first
          downloadedBytesCount = downloadResult.second

          if (!success || destFile.length() <= 0) {
            if (destFile.exists()) destFile.delete()
            success = false
          }
        } catch (e: Exception) {
          if (destFile.exists()) destFile.delete()
          success = false
        }
      }

      if (success) {
        val actualSize = formatBytes(downloadedBytesCount)
        val newItem = DownloadedItem(
          id = UUID.randomUUID().toString(),
          title = preview.title,
          author = preview.author,
          duration = preview.duration,
          size = actualSize,
          quality = selectedQuality.label,
          platform = preview.platform,
          date = "اليوم",
          isAudioOnly = selectedQuality.isAudioOnly,
          thumbnailUrl = preview.thumbnailUrl,
          localFilePath = destFile.absolutePath,
          contentUri = null,
          mimeType = mimeType,
          isExportedToGallery = false
        )

        withContext(Dispatchers.IO) {
          val existingDuplicates = downloadRepository.getAllEntitiesSync().filter {
            it.title.trim().equals(newItem.title.trim(), ignoreCase = true) &&
            it.isAudioOnly == newItem.isAudioOnly
          }
          for (dup in existingDuplicates) {
            downloadRepository.deleteById(dup.id)
          }
          downloadRepository.insert(newItem)
        }
        
        // Immediately remove video/audio from Home interface & clear input
        _previewData.value = null
        _urlInput.value = ""

        // Auto-export to gallery if user preference enabled
        if (_autoExportToGallery.value) {
          publishToGallery(newItem.id)
        }
        
        // Auto-navigate to History tab
        _navigateToHistoryEvent.tryEmit(Unit)
        
        _snackbarMessage.value = "تم اكتمال التحميل وحفظه داخل التطبيق بنجاح"
      } else {
        _snackbarMessage.value = AppStrings.downloadErrorMessage(_language.value)
      }

      _isDownloading.value = false
      _downloadProgress.value = 0f
      _downloadStatusDetail.value = ""
      updateRealCacheSize()
    }
  }

  // Requirement 2: Publish/Export specific item to MediaStore Gallery on demand
  fun publishToGallery(itemId: String) {
    val item = _historyList.value.firstOrNull { it.id == itemId } ?: return
    if (item.isExportedToGallery) {
      _snackbarMessage.value = AppStrings.inGallery(_language.value) + " ✓"
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      val app = getApplication<Application>()
      val resolver = app.contentResolver

      val sourceFile = item.localFilePath?.let { File(it) }
      if (sourceFile == null || !sourceFile.exists()) {
        withContext(Dispatchers.Main) {
          _snackbarMessage.value = AppStrings.mediaFileNotFound(_language.value)
        }
        return@launch
      }

      val isAudio = item.isAudioOnly
      val cleanTitle = (item.title.ifBlank { "media" })
        .replace(Regex("[^a-zA-Z0-9._\\-\\u0600-\\u06FF]"), "_")
        .take(35)
      val mime = item.mimeType ?: if (isAudio) "audio/mp4" else "video/mp4"
      val fileName = sourceFile.name
      val relativeFolder = if (isAudio) "Music/Khatif" else "Movies/Khatif"

      var insertedUri: Uri? = null
      var exportSuccess = false

      try {
        val collectionUri: Uri = if (isAudio) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
          } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
          }
        } else {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
          } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
          }
        }

        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
          put(MediaStore.MediaColumns.MIME_TYPE, mime)
          put(MediaStore.MediaColumns.TITLE, cleanTitle)
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeFolder)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
          }
        }

        insertedUri = resolver.insert(collectionUri, contentValues)
        if (insertedUri != null) {
          val outStream = resolver.openOutputStream(insertedUri)
          if (outStream != null) {
            sourceFile.inputStream().use { inStream ->
              inStream.copyTo(outStream)
            }
            outStream.flush()
            outStream.close()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
              val completeValues = ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
              }
              resolver.update(insertedUri, completeValues, null, null)
            }
            exportSuccess = true
          }
        }
      } catch (e: Exception) {
        insertedUri?.let {
          try { resolver.delete(it, null, null) } catch (_: Exception) {}
        }
        exportSuccess = false
      }

      // Fallback export if MediaStore insert failed
      if (!exportSuccess) {
        try {
          val publicFolder = Environment.getExternalStoragePublicDirectory(
            if (isAudio) Environment.DIRECTORY_MUSIC else Environment.DIRECTORY_MOVIES
          )
          val khatifFolder = File(publicFolder, "Khatif").apply { if (!exists()) mkdirs() }
          val publicFile = File(khatifFolder, fileName)
          sourceFile.copyTo(publicFile, overwrite = true)
          MediaScannerConnection.scanFile(app, arrayOf(publicFile.absolutePath), arrayOf(mime)) { _, scUri ->
            if (scUri != null && insertedUri == null) {
              insertedUri = scUri
            }
          }
          exportSuccess = publicFile.exists() && publicFile.length() > 0
        } catch (_: Exception) {
          exportSuccess = false
        }
      }

      if (exportSuccess) {
        downloadRepository.updateExportStatus(itemId, true, insertedUri?.toString())
      }

      withContext(Dispatchers.Main) {
        if (exportSuccess) {
          _historyList.update { list ->
            list.map {
              if (it.id == itemId) {
                it.copy(
                  isExportedToGallery = true,
                  contentUri = insertedUri?.toString() ?: it.contentUri
                )
              } else {
                it
              }
            }
          }
          _snackbarMessage.value = AppStrings.transferSuccessMessage(_language.value, item.isAudioOnly)
        } else {
          _snackbarMessage.value = AppStrings.publishedToGalleryError(_language.value)
        }
      }
    }
  }

  fun deleteHistoryItem(id: String) {
    val item = _historyList.value.firstOrNull { it.id == id }
    item?.let {
      if (!it.contentUri.isNullOrBlank()) {
        try {
          val uri = Uri.parse(it.contentUri)
          getApplication<Application>().contentResolver.delete(uri, null, null)
        } catch (_: Exception) {}
      }
      if (!it.localFilePath.isNullOrBlank()) {
        try {
          val file = File(it.localFilePath)
          if (file.exists()) file.delete()
        } catch (_: Exception) {}
      }
    }
    viewModelScope.launch(Dispatchers.IO) {
      downloadRepository.deleteById(id)
    }
    _historyList.update { current ->
      current.filterNot { it.id == id }
    }
    _snackbarMessage.value = AppStrings.itemDeletedMessage(_language.value)
  }

  fun renameHistoryItem(id: String, newTitle: String) {
    val cleanTitle = newTitle.trim()
    if (cleanTitle.isBlank()) return
    viewModelScope.launch(Dispatchers.IO) {
      downloadRepository.renameItem(id, cleanTitle)
    }
    _historyList.update { current ->
      current.map { if (it.id == id) it.copy(title = cleanTitle) else it }
    }
    _snackbarMessage.value = AppStrings.renameSuccessMessage(_language.value)
  }

  fun clearAllHistory() {
    val app = getApplication<Application>()
    _historyList.value.forEach { item ->
      if (!item.contentUri.isNullOrBlank()) {
        try {
          val uri = Uri.parse(item.contentUri)
          app.contentResolver.delete(uri, null, null)
        } catch (_: Exception) {}
      }
      if (!item.localFilePath.isNullOrBlank()) {
        try {
          val file = File(item.localFilePath)
          if (file.exists()) file.delete()
        } catch (_: Exception) {}
      }
    }
    viewModelScope.launch(Dispatchers.IO) {
      downloadRepository.deleteAll()
    }
    _historyList.value = emptyList()
    _snackbarMessage.value = AppStrings.historyClearedMessage(_language.value)
  }

  fun setHistoryFilter(filter: HistoryFilter) {
    _historyFilter.value = filter
  }

  fun setThemeMode(mode: ThemeMode) {
    _themeMode.value = mode
    prefs.edit().putString("pref_theme_mode", mode.name).apply()
  }

  fun setLanguage(lang: AppLanguage) {
    _language.value = lang
    prefs.edit().putString("pref_language", lang.name).apply()
  }

  fun setFontSizeScale(scale: Float) {
    val clamped = scale.coerceIn(0.85f, 1.35f)
    _fontSizeScale.value = clamped
    prefs.edit().putFloat("pref_font_scale", clamped).apply()
  }

  fun setDefaultQuality(quality: String) {
    _defaultQuality.value = quality
    prefs.edit().putString("pref_default_quality", quality).apply()
    _snackbarMessage.value = AppStrings.defaultQualitySetMessage(_language.value, quality)
  }

  fun setSavePath(newPath: String) {
    _savePath.value = newPath
    prefs.edit().putString("pref_save_path", newPath).apply()
    _snackbarMessage.value = AppStrings.savePathSetMessage(_language.value, newPath)
  }

  fun clearCache() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val cacheDir = getApplication<Application>().cacheDir
        cacheDir.deleteRecursively()
        cacheDir.mkdirs()
      } catch (_: Exception) {}
      updateRealCacheSize()
    }
    videoInfoCache.clear()
    _snackbarMessage.value = AppStrings.cacheClearedMessage(_language.value)
  }

  fun setWifiOnly(enabled: Boolean) {
    _wifiOnly.value = enabled
    prefs.edit().putBoolean("pref_wifi_only", enabled).apply()
  }

  fun setNotificationsEnabled(enabled: Boolean) {
    _notificationsEnabled.value = enabled
    prefs.edit().putBoolean("pref_notifications_enabled", enabled).apply()
  }

  fun setAutoAnalyzeOnPaste(enabled: Boolean) {
    _autoAnalyzeOnPaste.value = enabled
    prefs.edit().putBoolean("pref_auto_analyze_paste", enabled).apply()
  }

  fun setAutoExportToGallery(enabled: Boolean) {
    _autoExportToGallery.value = enabled
    prefs.edit().putBoolean("pref_auto_export_gallery", enabled).apply()
  }

  fun setHapticFeedbackEnabled(enabled: Boolean) {
    _hapticFeedbackEnabled.value = enabled
    prefs.edit().putBoolean("pref_haptic_feedback", enabled).apply()
  }

  fun clearAllHistory(deleteFiles: Boolean = true) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val allItems = downloadRepository.getAllEntitiesSync()
        if (deleteFiles) {
          for (item in allItems) {
            if (!item.localFilePath.isNullOrBlank()) {
              try { File(item.localFilePath).delete() } catch (_: Exception) {}
            }
          }
        }
        for (item in allItems) {
          downloadRepository.deleteById(item.id)
        }
        withContext(Dispatchers.Main) {
          _snackbarMessage.value = if (_language.value == AppLanguage.ARABIC) "تم مسح سجل التحميلات بنجاح" else "History cleared successfully"
        }
      } catch (_: Exception) {}
      updateRealCacheSize()
    }
  }

  fun dismissSnackbar() {
    _snackbarMessage.value = null
  }

  fun showSnackbar(message: String) {
    _snackbarMessage.value = message
  }

  private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
      gb >= 1.0 -> String.format(Locale.US, "%.1f GB", gb)
      mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
      kb >= 1.0 -> String.format(Locale.US, "%.0f KB", kb)
      else -> "$bytes B"
    }
  }

  private fun formatDuration(durationSeconds: Double?): String {
    if (durationSeconds == null || durationSeconds <= 0.0) return "00:00"
    val totalSec = durationSeconds.toLong()
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60
    return if (hours > 0) {
      String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
      String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }
  }

  private fun extractErrorDetail(errorBodyStr: String?): String? {
    if (errorBodyStr.isNullOrBlank()) return null
    return try {
      val json = JSONObject(errorBodyStr)
      json.optString("detail", null)
    } catch (_: Exception) {
      null
    }
  }
}
