package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaNotificationReceiver : BroadcastReceiver() {
  companion object {
    const val ACTION_PLAY_PAUSE = "com.example.ACTION_PLAY_PAUSE"
    const val ACTION_REWIND = "com.example.ACTION_REWIND"
    const val ACTION_FORWARD = "com.example.ACTION_FORWARD"
    const val ACTION_STOP = "com.example.ACTION_STOP"
  }

  override fun onReceive(context: Context, intent: Intent?) {
    val player = PipManager.activePlayer ?: return
    val item = PipManager.currentMediaItem

    when (intent?.action) {
      ACTION_PLAY_PAUSE -> {
        if (player.isPlaying) {
          player.pause()
        } else {
          player.play()
        }
        if (item != null) {
          MediaPlaybackNotificationManager.showNotification(context, item, player.isPlaying)
        }
      }
      ACTION_REWIND -> {
        val newPos = maxOf(0L, player.currentPosition - 10_000L)
        player.seekTo(newPos)
        if (item != null) {
          MediaPlaybackNotificationManager.showNotification(context, item, player.isPlaying)
        }
      }
      ACTION_FORWARD -> {
        val newPos = minOf(player.duration, player.currentPosition + 10_000L)
        player.seekTo(newPos)
        if (item != null) {
          MediaPlaybackNotificationManager.showNotification(context, item, player.isPlaying)
        }
      }
      ACTION_STOP -> {
        player.pause()
        MediaPlaybackNotificationManager.cancelNotification(context)
        PipManager.isPlaybackActive = false
      }
    }
  }
}
