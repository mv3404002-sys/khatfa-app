package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.model.DownloadedItem

object MediaPlaybackNotificationManager {
  private const val CHANNEL_ID = "media_playback_channel_v1"
  private const val NOTIFICATION_ID = 2024

  fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val name = "تشغيل الوسائط / Media Playback"
      val descriptionText = "إشعار التحكم في تشغيل الفيديو والصوت في الخلفية"
      val importance = NotificationManager.IMPORTANCE_LOW
      val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
        description = descriptionText
        setShowBadge(false)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun showNotification(context: Context, item: DownloadedItem, isPlaying: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
      ) {
        return
      }
    }

    createNotificationChannel(context)

    // Main tap intent to return to app
    val contentIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      0,
      contentIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Action Intents
    val rewindIntent = Intent(context, MediaNotificationReceiver::class.java).apply {
      action = MediaNotificationReceiver.ACTION_REWIND
    }
    val rewindPendingIntent = PendingIntent.getBroadcast(
      context,
      1,
      rewindIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val playPauseIntent = Intent(context, MediaNotificationReceiver::class.java).apply {
      action = MediaNotificationReceiver.ACTION_PLAY_PAUSE
    }
    val playPausePendingIntent = PendingIntent.getBroadcast(
      context,
      2,
      playPauseIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val forwardIntent = Intent(context, MediaNotificationReceiver::class.java).apply {
      action = MediaNotificationReceiver.ACTION_FORWARD
    }
    val forwardPendingIntent = PendingIntent.getBroadcast(
      context,
      3,
      forwardIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val stopIntent = Intent(context, MediaNotificationReceiver::class.java).apply {
      action = MediaNotificationReceiver.ACTION_STOP
    }
    val stopPendingIntent = PendingIntent.getBroadcast(
      context,
      4,
      stopIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
    val playPauseText = if (isPlaying) "إيقاف مؤقت" else "تشغيل"
    val statusText = if (isPlaying) "جاري التشغيل في الخلفية" else "متوقف مؤقتاً"

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(item.title)
      .setContentText("$statusText • ${item.duration}")
      .setSubText(if (item.isAudioOnly) "صوت" else "فيديو")
      .setContentIntent(contentPendingIntent)
      .setOngoing(isPlaying)
      .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .addAction(android.R.drawable.ic_media_rew, "-10ث", rewindPendingIntent)
      .addAction(playPauseIcon, playPauseText, playPausePendingIntent)
      .addAction(android.R.drawable.ic_media_ff, "+10ث", forwardPendingIntent)
      .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إغلاق", stopPendingIntent)
      .build()

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, notification)
  }

  fun cancelNotification(context: Context) {
    try {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.cancel(NOTIFICATION_ID)
    } catch (_: Exception) {}
  }
}
