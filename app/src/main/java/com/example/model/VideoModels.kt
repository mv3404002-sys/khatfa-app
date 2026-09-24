package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.InstagramPurple
import com.example.ui.theme.TikTokBlack
import com.example.ui.theme.TwitterBlack
import com.example.ui.theme.YouTubeRed

enum class PlatformType(
  val arabicName: String,
  val englishName: String,
  val brandColor: Color,
  val isUnderMaintenance: Boolean = false
) {
  YOUTUBE(
    arabicName = "يوتيوب",
    englishName = "YouTube",
    brandColor = YouTubeRed,
    isUnderMaintenance = true
  ),
  TIKTOK(
    arabicName = "تيك توك",
    englishName = "TikTok",
    brandColor = TikTokBlack,
    isUnderMaintenance = false
  ),
  INSTAGRAM(
    arabicName = "انستغرام",
    englishName = "Instagram",
    brandColor = InstagramPurple,
    isUnderMaintenance = true
  ),
  FACEBOOK(
    arabicName = "فيسبوك",
    englishName = "Facebook",
    brandColor = FacebookBlue,
    isUnderMaintenance = false
  ),
  TWITTER(
    arabicName = "تويتر / X",
    englishName = "X",
    brandColor = TwitterBlack,
    isUnderMaintenance = true
  );

  fun getName(lang: com.example.localization.AppLanguage): String = when (lang) {
    com.example.localization.AppLanguage.ARABIC -> arabicName
    else -> englishName
  }
}

data class VideoQualityOption(
  val id: String,
  val label: String,
  val size: String,
  val isAudioOnly: Boolean = false,
  val badge: String = "HD",
  val ext: String = "mp4",
  val mimeType: String = "video/mp4"
)

data class VideoPreviewData(
  val id: String,
  val title: String,
  val author: String,
  val duration: String,
  val platform: PlatformType,
  val originalUrl: String,
  val availableQualities: List<VideoQualityOption>,
  val selectedQualityId: String,
  val viewsCount: String = "1.2M",
  val likesCount: String = "85K",
  val thumbnailUrl: String? = null
)

data class DownloadedItem(
  val id: String,
  val title: String,
  val author: String,
  val duration: String,
  val size: String,
  val quality: String,
  val platform: PlatformType,
  val date: String,
  val isAudioOnly: Boolean = false,
  val thumbnailUrl: String? = null,
  val localFilePath: String? = null,
  val contentUri: String? = null,
  val mimeType: String? = null,
  val isExportedToGallery: Boolean = false
)
