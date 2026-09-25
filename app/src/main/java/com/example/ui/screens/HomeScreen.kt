package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.example.model.PlatformType
import com.example.ui.components.PlatformBadgeChip
import com.example.ui.components.QualitySelectorCard
import com.example.ui.components.VideoThumbnailView
import com.example.viewmodel.BackendConnectionStatus
import com.example.viewmodel.MainViewModel

@Composable
fun HomeScreen(
  viewModel: MainViewModel,
  onNavigateToHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val urlInput by viewModel.urlInput.collectAsState()
  val isAnalyzing by viewModel.isAnalyzing.collectAsState()
  val analysisElapsedSeconds by viewModel.analysisElapsedSeconds.collectAsState()
  val previewData by viewModel.previewData.collectAsState()
  val isDownloading by viewModel.isDownloading.collectAsState()
  val downloadProgress by viewModel.downloadProgress.collectAsState()
  val downloadStatusDetail by viewModel.downloadStatusDetail.collectAsState()
  val backendStatus by viewModel.backendStatus.collectAsState()
  val language by viewModel.language.collectAsState()
  val autoAnalyzeOnPaste by viewModel.autoAnalyzeOnPaste.collectAsState()
  val autoExportToGallery by viewModel.autoExportToGallery.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 18.dp),
    contentPadding = PaddingValues(top = 18.dp, bottom = 36.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    // Royal Brand Header for "Khatif"
    item {
      KhatifHeaderCard(
        language = language,
        backendStatus = backendStatus,
        onRetryBackend = { viewModel.checkBackendHealth() }
      )
    }

    // Input & Snatch Section (Clean, Focused, and Orderly)
    item {
      CleanKhatifInputCard(
        urlInput = urlInput,
        isAnalyzing = isAnalyzing,
        language = language,
        onUrlChange = { viewModel.onUrlChange(it) },
        onClear = { viewModel.clearUrl() },
        onPaste = {
          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
          val clip = clipboard?.primaryClip
          if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()?.trim() ?: ""
            if (text.isNotBlank()) {
              viewModel.pasteUrl(text)
            } else {
              viewModel.showClipboardEmptyMessage()
            }
          } else {
            viewModel.showClipboardEmptyMessage()
          }
        },
        onSnatch = { viewModel.snatchVideo() }
      )
    }

    // Quick Options & Automation Toggles in Home
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Quick Auto-Paste Chip
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (autoAnalyzeOnPaste) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (autoAnalyzeOnPaste) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { viewModel.setAutoAnalyzeOnPaste(!autoAnalyzeOnPaste) }
            .testTag("quick_toggle_auto_paste")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = null,
              tint = if (autoAnalyzeOnPaste) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = if (language == AppLanguage.ARABIC) "تحليل تلقائي" else if (language == AppLanguage.FRENCH) "Analyse auto" else "Auto-Analyze",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (autoAnalyzeOnPaste) FontWeight.Bold else FontWeight.Normal,
              color = if (autoAnalyzeOnPaste) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Quick Gallery Export Chip
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = if (autoExportToGallery) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, if (autoExportToGallery) MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { viewModel.setAutoExportToGallery(!autoExportToGallery) }
            .testTag("quick_toggle_auto_gallery")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CloudDownload,
              contentDescription = null,
              tint = if (autoExportToGallery) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = if (language == AppLanguage.ARABIC) "حفظ بالمعرض" else if (language == AppLanguage.FRENCH) "Vers galerie" else "To Gallery",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (autoExportToGallery) FontWeight.Bold else FontWeight.Normal,
              color = if (autoExportToGallery) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Main Content: Video Preview Card, Reassuring Analyzing Card, or Minimal Clean Ready Card
    item {
      AnimatedVisibility(
        visible = isAnalyzing,
        enter = fadeIn(tween(300)) + expandVertically(),
        exit = fadeOut(tween(200)) + shrinkVertically()
      ) {
        ReassuringAnalyzingCard(
          elapsedSeconds = analysisElapsedSeconds,
          language = language
        )
      }

      AnimatedVisibility(
        visible = previewData != null && !isAnalyzing,
        enter = fadeIn(tween(300)) + expandVertically(),
        exit = fadeOut(tween(200)) + shrinkVertically()
      ) {
        previewData?.let { preview ->
          RoyalVideoPreviewCard(
            preview = preview,
            isDownloading = isDownloading,
            downloadProgress = downloadProgress,
            downloadStatusDetail = downloadStatusDetail,
            language = language,
            onSelectQuality = { viewModel.selectQuality(it) },
            onDownload = { viewModel.startDownload() },
            onClear = { viewModel.clearUrl() }
          )
        }
      }

      AnimatedVisibility(
        visible = previewData == null && !isAnalyzing,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(200))
      ) {
        MinimalReadyStatusCard(
          language = language,
          onPlatformClick = { platform ->
            viewModel.showPlatformMaintenanceMessage(platform)
          }
        )
      }
    }
  }
}

@Composable
private fun KhatifHeaderCard(
  language: AppLanguage,
  backendStatus: BackendConnectionStatus,
  onRetryBackend: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp)
    ) {
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "Khatif",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Connection status dot only (green when connected, yellow when checking, red when offline)
          val (dotBg, dotColor) = when (backendStatus) {
            BackendConnectionStatus.ONLINE -> Pair(
              androidx.compose.ui.graphics.Color(0xFF2E7D32).copy(alpha = 0.18f),
              androidx.compose.ui.graphics.Color(0xFF2E7D32)
            )
            BackendConnectionStatus.CHECKING -> Pair(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
              MaterialTheme.colorScheme.primary
            )
            BackendConnectionStatus.OFFLINE -> Pair(
              MaterialTheme.colorScheme.error.copy(alpha = 0.18f),
              MaterialTheme.colorScheme.error
            )
          }

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(dotBg)
              .clickable { onRetryBackend() }
              .testTag("backend_status_dot")
          ) {
            Box(
              modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(dotColor)
            )
          }
        }
        Text(
          text = AppStrings.appTitle(language),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Black,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = AppStrings.appSubtitle(language),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(52.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary)
      ) {
        Icon(
          imageVector = Icons.Default.CloudDownload,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(26.dp)
        )
      }
    }
  }
}

@Composable
private fun CleanKhatifInputCard(
  urlInput: String,
  isAnalyzing: Boolean,
  language: AppLanguage,
  onUrlChange: (String) -> Unit,
  onClear: () -> Unit,
  onPaste: () -> Unit,
  onSnatch: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      OutlinedTextField(
        value = urlInput,
        onValueChange = onUrlChange,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("url_input_field"),
        shape = RoundedCornerShape(16.dp),
        placeholder = {
          Text(
            text = AppStrings.pastePlaceholder(language),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
          )
        },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
        },
        trailingIcon = {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            if (urlInput.isNotEmpty()) {
              IconButton(
                onClick = onClear,
                modifier = Modifier
                  .size(32.dp)
                  .testTag("clear_url_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = AppStrings.clearButton(language),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primaryContainer,
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { onPaste() }
                .testTag("paste_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.ContentPaste,
                  contentDescription = AppStrings.pasteButton(language),
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = AppStrings.pasteButton(language),
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
          focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        singleLine = true
      )

      Button(
        onClick = onSnatch,
        enabled = !isAnalyzing,
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("snatch_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
      ) {
        if (isAnalyzing) {
          CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(22.dp),
            strokeWidth = 2.5.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = AppStrings.analyzingText(language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        } else {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            modifier = Modifier.size(22.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = AppStrings.snatchButton(language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MinimalReadyStatusCard(
  language: AppLanguage,
  onPlatformClick: (PlatformType) -> Unit = {}
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer)
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.PlayCircleOutline,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      }
      Text(
        text = AppStrings.readyToSnatchTitle(language),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = AppStrings.readyToSnatchDesc(language),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 22.sp
      )

      FlowRow(
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        PlatformType.values().forEach { platform ->
          PlatformBadgeChip(
            platform = platform,
            language = language,
            onClick = { onPlatformClick(platform) },
            modifier = Modifier.padding(horizontal = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun RoyalVideoPreviewCard(
  preview: com.example.model.VideoPreviewData,
  isDownloading: Boolean,
  downloadProgress: Float,
  downloadStatusDetail: String,
  language: AppLanguage,
  onSelectQuality: (String) -> Unit,
  onDownload: () -> Unit,
  onClear: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("video_preview_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        ) {
          Text(
            text = AppStrings.previewTitle(language),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }
        IconButton(
          onClick = onClear,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = AppStrings.clearButton(language),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Thumbnail with real thumbnail URL from backend
      VideoThumbnailView(
        title = preview.title,
        duration = preview.duration,
        platform = preview.platform,
        thumbnailUrl = preview.thumbnailUrl,
        modifier = Modifier
          .fillMaxWidth()
          .height(190.dp)
      )

      // Title & Channel
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = preview.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = preview.author,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
          )
          Text(
            text = AppStrings.viewsCountLabel(language, preview.viewsCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      val selectedOption = preview.availableQualities.firstOrNull { it.id == preview.selectedQualityId }
        ?: preview.availableQualities.first()
      val isAudioSelected = selectedOption.isAudioOnly

      // 1. Media Type Mode Tabs (Video vs Audio)
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
        ) {
          // Tab 1: Video (Exactly 2 options: Best Quality for Phone & HD)
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (!isAudioSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                val videoOpt = preview.availableQualities.firstOrNull { !it.isAudioOnly }
                if (videoOpt != null) onSelectQuality(videoOpt.id)
              }
              .testTag("tab_video_quality")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                tint = if (!isAudioSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = AppStrings.tabVideo(language),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (!isAudioSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Tab 2: Audio Only
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isAudioSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(10.dp))
              .clickable {
                val audioOpt = preview.availableQualities.firstOrNull { it.isAudioOnly }
                if (audioOpt != null) onSelectQuality(audioOpt.id)
              }
              .testTag("tab_audio_quality")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Headphones,
                contentDescription = null,
                tint = if (isAudioSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = AppStrings.tabAudio(language),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isAudioSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // 2. Streamlined Quality Options: Exactly 2 options for video (Best for Phone & HD) or Audio
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val displayedQualities = if (isAudioSelected) {
          preview.availableQualities.filter { it.isAudioOnly }
        } else {
          // Exactly the TWO Video Qualities: Best Quality for Phone & HD Quality
          preview.availableQualities.filter { !it.isAudioOnly }
        }

        displayedQualities.forEach { quality ->
          QualitySelectorCard(
            option = quality,
            isSelected = quality.id == preview.selectedQualityId,
            language = language,
            onSelect = { onSelectQuality(quality.id) }
          )
        }
      }

      // Progress bar if downloading
      if (isDownloading) {
        val animatedProgress by animateFloatAsState(
          targetValue = downloadProgress.coerceIn(0f, 1f),
          animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
          label = "smooth_download_progress"
        )
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = if (downloadStatusDetail.isNotBlank()) downloadStatusDetail else AppStrings.downloading(language),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "${(downloadProgress * 100).toInt()}%",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
          LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
          )
        }
      }

      // Action Buttons
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
          onClick = onDownload,
          enabled = !isDownloading,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("download_now_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          )
        ) {
          Icon(
            imageVector = if (isAudioSelected) Icons.Default.Headphones else Icons.Default.CloudDownload,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isDownloading) {
              AppStrings.downloading(language)
            } else if (isAudioSelected) {
              AppStrings.audioOnlyQualityLabel(language)
            } else {
              AppStrings.downloadNow(language)
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        OutlinedButton(
          onClick = onClear,
          enabled = !isDownloading,
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
          Text(
            text = AppStrings.snatchAnother(language),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun ReassuringAnalyzingCard(
  elapsedSeconds: Int,
  language: AppLanguage
) {
  val tipText = when {
    elapsedSeconds <= 7 -> AppStrings.analyzingText(language)
    elapsedSeconds in 8..20 -> AppStrings.analyzingTipColdStart(language)
    elapsedSeconds in 21..35 -> AppStrings.analyzingTipExtracting(language)
    else -> AppStrings.analyzingTipFinishing(language)
  }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("analyzing_reassurance_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        CircularProgressIndicator(
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp),
          strokeWidth = 2.5.dp
        )
        Text(
          text = AppStrings.analyzingText(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primaryContainer
        ) {
          Text(
            text = "$elapsedSeconds ${AppStrings.secondsUnit(language)}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      LinearProgressIndicator(
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primaryContainer
      )

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = tipText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )
        }
      }
    }
  }
}
