package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Forward10
import androidx.media3.common.PlaybackParameters
import com.example.util.MediaPlaybackNotificationManager
import com.example.util.MediaShareUtil
import com.example.util.PipManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.media3.common.VideoSize
import androidx.media3.ui.AspectRatioFrameLayout
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.DownloadedItem
import kotlinx.coroutines.delay
import java.io.File
import java.util.Locale

@OptIn(UnstableApi::class)
@Composable
fun InternalMediaPlayerModal(
  item: DownloadedItem,
  language: AppLanguage,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current

  // Resolve media URI
  val mediaUri = remember(item) {
    if (!item.localFilePath.isNullOrBlank()) {
      val file = File(item.localFilePath)
      if (file.exists() && file.length() > 0) {
        try {
          FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (_: Exception) {
          Uri.fromFile(file)
        }
      } else if (!item.contentUri.isNullOrBlank()) {
        Uri.parse(item.contentUri)
      } else {
        null
      }
    } else if (!item.contentUri.isNullOrBlank()) {
      Uri.parse(item.contentUri)
    } else {
      null
    }
  }

  val exoPlayer = remember(context) {
    val loadControl = androidx.media3.exoplayer.DefaultLoadControl.Builder()
      .setBufferDurationsMs(
        15_000,
        50_000,
        250,
        500
      )
      .build()

    ExoPlayer.Builder(context)
      .setLoadControl(loadControl)
      .build().apply {
        repeatMode = Player.REPEAT_MODE_OFF
      }
  }

  var isPlaying by remember { mutableStateOf(false) }
  var currentPosition by remember { mutableLongStateOf(0L) }
  var totalDuration by remember { mutableLongStateOf(0L) }
  var isDraggingSlider by remember { mutableStateOf(false) }
  var sliderPosition by remember { mutableFloatStateOf(0f) }
  var videoWidth by remember { mutableIntStateOf(0) }
  var videoHeight by remember { mutableIntStateOf(0) }
  var showInfoDialog by remember { mutableStateOf(false) }
  var dragOffsetY by remember { mutableFloatStateOf(0f) }

  val defaultInitialSpeed = remember(context) {
    context.getSharedPreferences("khatif_user_settings", Context.MODE_PRIVATE)
      .getFloat("pref_default_playback_speed", 1.0f)
  }
  var playbackSpeed by remember { mutableFloatStateOf(defaultInitialSpeed) }
  var isLooping by remember { mutableStateOf(false) }

  val isInPip by PipManager.isInPipMode.collectAsState()
  var isControlsLocked by remember { mutableStateOf(false) }
  var areControlsVisible by remember { mutableStateOf(true) }
  var seekFeedbackText by remember { mutableStateOf<String?>(null) }
  var seekFeedbackIsForward by remember { mutableStateOf(true) }

  // Auto-hide controls after 4 seconds of inactivity if playing
  LaunchedEffect(areControlsVisible, isPlaying, isControlsLocked) {
    if (areControlsVisible && isPlaying && !isControlsLocked) {
      kotlinx.coroutines.delay(4000L)
      areControlsVisible = false
    }
  }

  // Clear seek feedback indicator after 800ms
  LaunchedEffect(seekFeedbackText) {
    if (seekFeedbackText != null) {
      kotlinx.coroutines.delay(800L)
      seekFeedbackText = null
    }
  }

  // Handle Playback Speed
  LaunchedEffect(playbackSpeed) {
    exoPlayer.playbackParameters = PlaybackParameters(playbackSpeed)
  }

  // Handle Loop Mode
  LaunchedEffect(isLooping) {
    exoPlayer.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
  }

  // Listen to playback state and video dimensions & sync PipManager / Notifications
  DisposableEffect(exoPlayer) {
    PipManager.isModalOpen = true
    PipManager.isPlaybackActive = isPlaying
    PipManager.isVideo = !item.isAudioOnly
    PipManager.activePlayer = exoPlayer
    PipManager.currentMediaItem = item
    if (videoWidth > 0 && videoHeight > 0) {
      PipManager.videoWidth = videoWidth
      PipManager.videoHeight = videoHeight
    }
    (context as? Activity)?.let { PipManager.syncPipParams(it) }

    val listener = object : Player.Listener {
      override fun onIsPlayingChanged(playing: Boolean) {
        isPlaying = playing
        PipManager.isPlaybackActive = playing
        (context as? Activity)?.let { PipManager.syncPipParams(it) }
        MediaPlaybackNotificationManager.showNotification(context, item, playing)
      }

      override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
          val dur = exoPlayer.duration
          if (dur > 0) totalDuration = dur
        } else if (playbackState == Player.STATE_ENDED) {
          isPlaying = false
          PipManager.isPlaybackActive = false
          (context as? Activity)?.let { PipManager.syncPipParams(it) }
          MediaPlaybackNotificationManager.showNotification(context, item, false)
        }
      }

      override fun onVideoSizeChanged(videoSize: VideoSize) {
        if (videoSize.width > 0 && videoSize.height > 0) {
          videoWidth = videoSize.width
          videoHeight = videoSize.height
          PipManager.videoWidth = videoSize.width
          PipManager.videoHeight = videoSize.height
          (context as? Activity)?.let { PipManager.syncPipParams(it) }
        }
      }
    }
    exoPlayer.addListener(listener)

    onDispose {
      PipManager.disableAutoPip(context as? Activity)
      PipManager.activePlayer = null
      PipManager.currentMediaItem = null
      MediaPlaybackNotificationManager.cancelNotification(context)
      exoPlayer.removeListener(listener)
      exoPlayer.stop()
      exoPlayer.release()
    }
  }

  // Prepare player when URI changes
  LaunchedEffect(mediaUri) {
    if (mediaUri != null) {
      val mediaItem = MediaItem.fromUri(mediaUri)
      exoPlayer.setMediaItem(mediaItem)
      exoPlayer.prepare()
      exoPlayer.playWhenReady = true
    }
  }

  // Periodic position update
  LaunchedEffect(isPlaying, isDraggingSlider) {
    while (isPlaying && !isDraggingSlider) {
      currentPosition = exoPlayer.currentPosition
      val dur = exoPlayer.duration
      if (dur > 0) totalDuration = dur
      delay(250L)
    }
  }

  // Pre-calculate guaranteed navigation bar clearance from caller window insets
  val callerNavBars = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
  val callerSystemBars = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
  val guaranteedBottomMargin = maxOf(callerNavBars, callerSystemBars, 56.dp) + 36.dp

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      dismissOnBackPress = true,
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    val dialogView = LocalView.current
    SideEffect {
      var parent = dialogView.parent
      while (parent != null && parent !is DialogWindowProvider) {
        parent = parent.parent
      }
      (parent as? DialogWindowProvider)?.window?.let { window ->
        WindowCompat.setDecorFitsSystemWindows(window, false)
      }
    }

    val dialogNavBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val dialogSystemBottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val effectiveBottomPadding = maxOf(dialogNavBottom, dialogSystemBottom, guaranteedBottomMargin) + 16.dp

    if (isInPip) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black),
        contentAlignment = Alignment.Center
      ) {
        if (item.isAudioOnly) {
          Row(
            modifier = Modifier
              .fillMaxSize()
              .background(Color(0xFF0F121A))
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.GraphicEq,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.title,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "${formatDurationMs(currentPosition)} / ${formatDurationMs(totalDuration)}",
                color = Color(0xFFA0ABBC),
                fontSize = 9.sp
              )
            }
          }
        } else {
          AndroidView(
            factory = { ctx ->
              PlayerView(ctx).apply {
                this.player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                setBackgroundColor(android.graphics.Color.BLACK)
                setShutterBackgroundColor(android.graphics.Color.BLACK)
              }
            },
            update = { playerView ->
              if (playerView.player != exoPlayer) {
                playerView.player = exoPlayer
              }
            },
            modifier = Modifier.fillMaxSize()
          )
        }
      }
      return@Dialog
    }

    Surface(
      modifier = Modifier
        .fillMaxSize()
        .testTag("internal_player_fullscreen_modal"),
      color = Color.Black
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black)
          .offset { IntOffset(0, dragOffsetY.roundToInt().coerceAtLeast(0)) }
          .pointerInput(Unit) {
            detectVerticalDragGestures(
              onVerticalDrag = { change, dragAmount ->
                if (dragAmount > 0 || dragOffsetY > 0) {
                  dragOffsetY = (dragOffsetY + dragAmount).coerceAtLeast(0f)
                  change.consume()
                }
              },
              onDragEnd = {
                if (dragOffsetY > 160f) {
                  onDismiss()
                } else {
                  dragOffsetY = 0f
                }
              },
              onDragCancel = {
                dragOffsetY = 0f
              }
            )
          }
          .statusBarsPadding()
          .padding(horizontal = 14.dp)
          .padding(top = 6.dp, bottom = effectiveBottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Drag handle indicator for Swipe-Down-To-Dismiss
        Box(
          modifier = Modifier
            .padding(bottom = 6.dp)
            .width(42.dp)
            .height(5.dp)
            .clip(CircleShape)
            .background(Color(0x66FFFFFF))
        )

        // 1. Top Bar with Clear Back Button, Info Button, and Media Type
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          // Back Button
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1B1E26),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A313E)),
            modifier = Modifier
              .clip(RoundedCornerShape(14.dp))
              .clickable { onDismiss() }
              .testTag("back_to_history_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = AppStrings.backToHistory(language),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = AppStrings.backToHistory(language),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // PiP Button (Floating mini player mode)
            if (!item.isAudioOnly && !isControlsLocked) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1B1E26),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A313E)),
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .clickable {
                    (context as? Activity)?.let { act ->
                      PipManager.enterPip(act)
                    }
                  }
                  .testTag("player_pip_button")
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.PictureInPictureAlt,
                    contentDescription = AppStrings.pipModeLabel(language),
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                  )
                }
              }
            }

            // Lock/Unlock Controls Button
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isControlsLocked) MaterialTheme.colorScheme.primaryContainer else Color(0xFF1B1E26),
              border = androidx.compose.foundation.BorderStroke(1.dp, if (isControlsLocked) MaterialTheme.colorScheme.primary else Color(0xFF2A313E)),
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  isControlsLocked = !isControlsLocked
                  if (isControlsLocked) {
                    areControlsVisible = false
                  }
                }
                .testTag("player_lock_button")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (isControlsLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                  contentDescription = if (isControlsLocked) AppStrings.unlockControlsLabel(language) else AppStrings.lockControlsLabel(language),
                  tint = if (isControlsLocked) MaterialTheme.colorScheme.primary else Color.White,
                  modifier = Modifier.size(17.dp)
                )
              }
            }

            if (!isControlsLocked) {
              // Share Button
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1B1E26),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A313E)),
                modifier = Modifier
                  .size(36.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .clickable {
                    MediaShareUtil.shareMediaItem(context, item, language)
                  }
                  .testTag("player_share_button")
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = AppStrings.shareButton(language),
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                  )
                }
              }

              // Info Button (ⓘ) inside player
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1B1E26),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2A313E)),
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { showInfoDialog = true }
                  .testTag("player_info_button")
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = AppStrings.mediaInfoTitle(language),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                  Text(
                    text = AppStrings.mediaInfoTitle(language),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }

              // Media Type badge
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF161920),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF242A36))
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(item.platform.brandColor)
                  )
                  Text(
                    text = if (item.isAudioOnly) AppStrings.filterAudio(language) else AppStrings.filterVideo(language),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB4BCC8)
                  )
                }
              }
            }
          }
        }

        // 2. Video Container: Strictly bounded by weight(1f), centered, solid pure black, NO blur, NO stretching
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.Black)
            .pointerInput(isControlsLocked) {
              detectTapGestures(
                onTap = {
                  if (!isControlsLocked) {
                    areControlsVisible = !areControlsVisible
                  }
                },
                onDoubleTap = { offset ->
                  if (!isControlsLocked) {
                    val isRightSide = offset.x >= size.width / 2f
                    if (isRightSide) {
                      val newPos = minOf(totalDuration, exoPlayer.currentPosition + 10_000L)
                      exoPlayer.seekTo(newPos)
                      currentPosition = newPos
                      seekFeedbackIsForward = true
                      seekFeedbackText = "+10"
                    } else {
                      val newPos = maxOf(0L, exoPlayer.currentPosition - 10_000L)
                      exoPlayer.seekTo(newPos)
                      currentPosition = newPos
                      seekFeedbackIsForward = false
                      seekFeedbackText = "-10"
                    }
                  }
                }
              )
            }
            .onGloballyPositioned { coordinates ->
              val pos = coordinates.positionInWindow()
              val size = coordinates.size
              if (size.width > 0 && size.height > 0) {
                val rect = android.graphics.Rect(
                  pos.x.toInt(),
                  pos.y.toInt(),
                  (pos.x + size.width).toInt(),
                  (pos.y + size.height).toInt()
                )
                PipManager.sourceRectHint = rect
                (context as? Activity)?.let { PipManager.syncPipParams(it) }
              }
            }
            .testTag("player_media_frame"),
          contentAlignment = Alignment.Center
        ) {
          if (item.isAudioOnly) {
            AudioVisualizerPlayerHero(
              item = item,
              isPlaying = isPlaying
            )
          } else {
            AndroidView(
              factory = { ctx ->
                PlayerView(ctx).apply {
                  this.player = exoPlayer
                  useController = false
                  resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                  setBackgroundColor(android.graphics.Color.BLACK)
                  setShutterBackgroundColor(android.graphics.Color.BLACK)
                }
              },
              update = { playerView ->
                if (playerView.player != exoPlayer) {
                  playerView.player = exoPlayer
                }
              },
              modifier = Modifier.fillMaxSize()
            )
          }

          // Double Tap Seek Overlay Feedback
          if (seekFeedbackText != null) {
            Box(
              modifier = Modifier
                .align(if (seekFeedbackIsForward) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(horizontal = 36.dp)
                .size(76.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.75f))
                .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = if (seekFeedbackIsForward) Icons.Default.Forward10 else Icons.Default.Replay10,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(32.dp)
                )
                Text(
                  text = seekFeedbackText ?: "",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        // 3. Fixed Bottom Media Controls: Stationary directly on display screen, animated visibility
        AnimatedVisibility(
          visible = areControlsVisible && !isControlsLocked,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp)
              .padding(top = 4.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Media Title Preview directly on screen
            Text(
              text = item.title,
              style = MaterialTheme.typography.bodySmall,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              modifier = Modifier.fillMaxWidth()
            )

            // Progress Slider and Times
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
              val progressVal = if (isDraggingSlider) {
                sliderPosition
              } else {
                if (totalDuration > 0) (currentPosition.toFloat() / totalDuration).coerceIn(0f, 1f) else 0f
              }

              Slider(
                value = progressVal,
                onValueChange = {
                  isDraggingSlider = true
                  sliderPosition = it
                },
                onValueChangeFinished = {
                  val targetMs = (sliderPosition * totalDuration).toLong()
                  exoPlayer.seekTo(targetMs)
                  currentPosition = targetMs
                  isDraggingSlider = false
                },
                colors = SliderDefaults.colors(
                  thumbColor = MaterialTheme.colorScheme.primary,
                  activeTrackColor = MaterialTheme.colorScheme.primary,
                  inactiveTrackColor = Color(0x55FFFFFF)
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("player_progress_slider")
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = formatDurationMs(currentPosition),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = formatDurationMs(totalDuration),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFA0ABBC)
                )
              }
            }

            // Controls Row (Loop, 10s Rewind, Play/Pause, 10s Forward, Speed)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Loop Toggle Button
              IconButton(
                onClick = { isLooping = !isLooping },
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(if (isLooping) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f) else Color(0x551E2432))
                  .border(1.dp, if (isLooping) MaterialTheme.colorScheme.primary else Color(0x33FFFFFF), CircleShape)
                  .testTag("loop_toggle_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Repeat,
                  contentDescription = AppStrings.loopModeLabel(language),
                  tint = if (isLooping) MaterialTheme.colorScheme.primary else Color.White,
                  modifier = Modifier.size(22.dp)
                )
              }

              // Rewind 10s Button
              IconButton(
                onClick = {
                  val newPos = maxOf(0L, exoPlayer.currentPosition - 10_000L)
                  exoPlayer.seekTo(newPos)
                  currentPosition = newPos
                },
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(Color(0x991E2432))
                  .border(1.5.dp, Color(0x55FFFFFF), CircleShape)
                  .testTag("rewind_10s_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Replay10,
                  contentDescription = "-10",
                  tint = Color.White,
                  modifier = Modifier.size(26.dp)
                )
              }

              // Main Play/Pause Button
              IconButton(
                onClick = {
                  if (isPlaying) {
                    exoPlayer.pause()
                  } else {
                    if (exoPlayer.playbackState == Player.STATE_ENDED) {
                      exoPlayer.seekTo(0)
                    }
                    exoPlayer.play()
                  }
                },
                modifier = Modifier
                  .size(70.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary)
                  .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                  .testTag("play_pause_button")
              ) {
                Icon(
                  imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isPlaying) AppStrings.pauseButton(language) else AppStrings.playButton(language),
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(38.dp)
                )
              }

              // Forward 10s Button
              IconButton(
                onClick = {
                  val newPos = minOf(totalDuration, exoPlayer.currentPosition + 10_000L)
                  exoPlayer.seekTo(newPos)
                  currentPosition = newPos
                },
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(Color(0x991E2432))
                  .border(1.5.dp, Color(0x55FFFFFF), CircleShape)
                  .testTag("forward_10s_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Forward10,
                  contentDescription = "+10",
                  tint = Color.White,
                  modifier = Modifier.size(26.dp)
                )
              }

              // Playback Speed Selector Pill (1.0x -> 1.25x -> 1.5x -> 2.0x -> 0.75x -> 1.0x)
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0x991E2432),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = Modifier
                  .height(44.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .clickable {
                    playbackSpeed = when (playbackSpeed) {
                      1.0f -> 1.25f
                      1.25f -> 1.5f
                      1.5f -> 2.0f
                      2.0f -> 0.75f
                      else -> 1.0f
                    }
                  }
                  .testTag("speed_toggle_button")
              ) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                  Text(
                    text = "${playbackSpeed}x",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }
            }
          }
        }
    }
    }

    // Requirement 3: Info Dialog inside the player
    if (showInfoDialog) {
      AlertDialog(
        onDismissRequest = { showInfoDialog = false },
        icon = {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
          )
        },
        title = {
          Text(
            text = AppStrings.mediaInfoTitle(language),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
          )
        },
        text = {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = item.title,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            PlayerInfoLine(label = AppStrings.fileSizeLabel(language), value = item.size)
            PlayerInfoLine(
              label = AppStrings.durationLabel(language),
              value = if (totalDuration > 0) formatDurationMs(totalDuration) else item.duration
            )
            PlayerInfoLine(label = AppStrings.downloadDateLabel(language), value = item.date)
            PlayerInfoLine(label = AppStrings.qualityLabel(language), value = item.quality)
            PlayerInfoLine(label = AppStrings.platformLabel(language), value = item.platform.getName(language))
            if (!item.localFilePath.isNullOrBlank()) {
              PlayerInfoLine(label = AppStrings.storagePathLabel(language), value = item.localFilePath)
            }
          }
        },
        confirmButton = {
          TextButton(
            onClick = { showInfoDialog = false },
            modifier = Modifier.testTag("dismiss_info_dialog_button")
          ) {
            Text(
              text = AppStrings.closeButton(language),
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      )
    }
  }
}

@Composable
private fun PlayerInfoLine(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface
    )
  }
}

@Composable
private fun AudioVisualizerPlayerHero(
  item: DownloadedItem,
  isPlaying: Boolean
) {
  val infiniteTransition = rememberInfiniteTransition(label = "disc_spin")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "disc_rotation"
  )

  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.radialGradient(
          colors = listOf(
            Color(0xFF242C3B),
            Color(0xFF0F1116)
          )
        )
      )
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(190.dp)
        .rotate(if (isPlaying) rotation else 0f)
        .clip(CircleShape)
        .background(Color(0xFF14171E))
        .border(3.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), CircleShape)
    ) {
      if (!item.thumbnailUrl.isNullOrBlank()) {
        AsyncImage(
          model = item.thumbnailUrl,
          contentDescription = item.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
        )
      } else {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
          Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(50.dp)
          )
        }
      }
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(Color.Black)
          .border(2.5.dp, Color.White, CircleShape)
      )
    }
  }
}

private fun formatDurationMs(ms: Long): String {
  if (ms <= 0) return "00:00"
  val totalSeconds = ms / 1000
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  val hours = minutes / 60
  return if (hours > 0) {
    String.format(Locale.US, "%02d:%02d:%02d", hours, minutes % 60, seconds)
  } else {
    String.format(Locale.US, "%02d:%02d", minutes, seconds)
  }
}
