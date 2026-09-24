package com.example

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.navigation.MainAppContainer
import com.example.ui.theme.VidSnatchTheme
import com.example.util.MediaPlaybackNotificationManager
import com.example.util.PipManager
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    handleIncomingShareIntent(intent)

    setContent {
      val themeMode by viewModel.themeMode.collectAsState()
      val language by viewModel.language.collectAsState()
      val fontScale by viewModel.fontSizeScale.collectAsState()

      VidSnatchTheme(
        themeMode = themeMode,
        language = language,
        fontScale = fontScale
      ) {
        MainAppContainer(viewModel = viewModel)
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleIncomingShareIntent(intent)
  }

  override fun onUserLeaveHint() {
    super.onUserLeaveHint()
    // ONLY enter PiP when exiting while actively watching/listening to media:
    if (PipManager.isModalOpen && PipManager.isPlaybackActive) {
      PipManager.enterPip(this)
      // Show rich media notification in top status bar
      PipManager.currentMediaItem?.let { item ->
        val isPlaying = PipManager.activePlayer?.isPlaying == true
        MediaPlaybackNotificationManager.showNotification(this, item, isPlaying)
      }
    }
  }

  override fun onPictureInPictureModeChanged(
    isInPictureInPictureMode: Boolean,
    newConfig: Configuration
  ) {
    super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    PipManager.setInPipMode(isInPictureInPictureMode)
    if (!isInPictureInPictureMode && !PipManager.isPlaybackActive) {
      PipManager.disableAutoPip(this)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (!PipManager.isPlaybackActive) {
      MediaPlaybackNotificationManager.cancelNotification(this)
    }
  }

  private fun handleIncomingShareIntent(intent: Intent?) {
    if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
      val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
      if (!sharedText.isNullOrBlank()) {
        viewModel.onUrlChange(sharedText.trim())
      }
    }
  }
}
