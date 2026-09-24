package com.example.util

import android.app.Activity
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.media3.common.Player
import com.example.model.DownloadedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object PipManager {
  private val _isInPipMode = MutableStateFlow(false)
  val isInPipMode = _isInPipMode.asStateFlow()

  var isModalOpen: Boolean = false
  var isPlaybackActive: Boolean = false
  var isVideo: Boolean = false
  var videoWidth: Int = 16
  var videoHeight: Int = 9
  var activePlayer: Player? = null
  var currentMediaItem: DownloadedItem? = null
  var sourceRectHint: android.graphics.Rect? = null

  fun setInPipMode(inPip: Boolean) {
    _isInPipMode.value = inPip
  }

  fun enterPip(activity: Activity): Boolean {
    // Only enter PiP if the media player is currently open and playing
    if (!isModalOpen || !isPlaybackActive) return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return try {
        val params = buildPipParams(activity)
        activity.enterPictureInPictureMode(params)
      } catch (e: Exception) {
        false
      }
    }
    return false
  }

  fun updatePipParams(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && _isInPipMode.value) {
      try {
        val params = buildPipParams(activity)
        activity.setPictureInPictureParams(params)
      } catch (_: Exception) {}
    }
  }

  fun syncPipParams(activity: Activity?) {
    if (activity == null) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      try {
        val params = buildPipParams(activity)
        activity.setPictureInPictureParams(params)
      } catch (_: Exception) {}
    }
  }

  fun disableAutoPip(activity: Activity?) {
    isModalOpen = false
    isPlaybackActive = false
    sourceRectHint = null
    if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      try {
        val params = PictureInPictureParams.Builder()
          .setAutoEnterEnabled(false)
          .build()
        activity.setPictureInPictureParams(params)
      } catch (_: Exception) {}
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun buildPipParams(context: Context): PictureInPictureParams {
    val builder = PictureInPictureParams.Builder()

    // Aspect ratio: for audio use a wide compact strip (2.38:1), for video use safe aspect ratio
    val (num, den) = if (isVideo) {
      computeSafeAspectRatio(videoWidth, videoHeight)
    } else {
      // Audio strip: wide horizontal format positioned like a status bar banner
      238 to 100
    }
    builder.setAspectRatio(Rational(num, den))

    // Set source rect hint so Android animates directly into the top player frame near the camera
    sourceRectHint?.let { rect ->
      if (!rect.isEmpty) {
        builder.setSourceRectHint(rect)
      }
    }

    // Add native actions if available (Play/Pause, Rewind, Forward)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val isPlaying = activePlayer?.isPlaying == true
      val playPauseIcon = if (isPlaying) {
        android.R.drawable.ic_media_pause
      } else {
        android.R.drawable.ic_media_play
      }
      val playPauseTitle = if (isPlaying) "Pause" else "Play"

      val playPauseIntent = Intent(context, MediaNotificationReceiver::class.java).apply {
        action = MediaNotificationReceiver.ACTION_PLAY_PAUSE
      }
      val playPausePendingIntent = PendingIntent.getBroadcast(
        context,
        100,
        playPauseIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val playPauseAction = RemoteAction(
        Icon.createWithResource(context, playPauseIcon),
        playPauseTitle,
        playPauseTitle,
        playPausePendingIntent
      )

      builder.setActions(listOf(playPauseAction))
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      // Auto-enter PiP ONLY if modal is currently open and playing
      builder.setAutoEnterEnabled(isModalOpen && isPlaybackActive)
      builder.setSeamlessResizeEnabled(true)
    }

    return builder.build()
  }

  private fun computeSafeAspectRatio(w: Int, h: Int): Pair<Int, Int> {
    if (w <= 0 || h <= 0) return 16 to 9
    val ratio = w.toFloat() / h.toFloat()
    // Android limits aspect ratio between ~0.418 and ~2.39
    return when {
      ratio < 0.42f -> 42 to 100
      ratio > 2.38f -> 238 to 100
      else -> {
        val gcdVal = gcd(w, h)
        if (gcdVal > 0) (w / gcdVal) to (h / gcdVal) else 16 to 9
      }
    }
  }

  private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}
