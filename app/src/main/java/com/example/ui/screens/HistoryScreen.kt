package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.DownloadedItem
import com.example.model.PlatformType
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.InternalMediaPlayerModal
import com.example.viewmodel.HistoryFilter
import com.example.viewmodel.MainViewModel
import java.io.File

enum class VaultSortCriteria {
  DATE_DESC,
  SIZE_DESC,
  NAME_ASC
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HistoryScreen(
  viewModel: MainViewModel,
  onNavigateToHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val rawHistoryList by viewModel.historyList.collectAsState()
  val activeFilter by viewModel.historyFilter.collectAsState()
  val language by viewModel.language.collectAsState()

  var searchQuery by rememberSaveable { mutableStateOf("") }
  var sortCriteria by rememberSaveable { mutableStateOf(VaultSortCriteria.DATE_DESC) }
  var selectedPlatformFilter by rememberSaveable { mutableStateOf<PlatformType?>(null) }

  val historyList = remember(rawHistoryList, activeFilter, searchQuery, sortCriteria, selectedPlatformFilter) {
    var list = rawHistoryList

    // 1. Filter by media type
    list = when (activeFilter) {
      HistoryFilter.ALL -> list
      HistoryFilter.VIDEO -> list.filter { !it.isAudioOnly }
      HistoryFilter.AUDIO -> list.filter { it.isAudioOnly }
    }

    // 2. Filter by platform
    if (selectedPlatformFilter != null) {
      list = list.filter { it.platform == selectedPlatformFilter }
    }

    // 3. Search query
    if (searchQuery.isNotBlank()) {
      val query = searchQuery.trim().lowercase()
      list = list.filter {
        it.title.lowercase().contains(query) ||
        (it.author?.lowercase()?.contains(query) == true)
      }
    }

    // 4. Sort
    when (sortCriteria) {
      VaultSortCriteria.DATE_DESC -> list
      VaultSortCriteria.SIZE_DESC -> list.sortedByDescending { parseSizeToBytes(it.size) }
      VaultSortCriteria.NAME_ASC -> list.sortedBy { it.title.lowercase() }
    }
  }

  val context = LocalContext.current
  var isGridView by rememberSaveable { mutableStateOf(false) }
  var showClearConfirmDialog by remember { mutableStateOf(false) }
  var itemToDelete by remember { mutableStateOf<DownloadedItem?>(null) }
  var activePlaybackItem by remember { mutableStateOf<DownloadedItem?>(null) }
  var renameTargetItem by remember { mutableStateOf<DownloadedItem?>(null) }
  var renameInputText by remember { mutableStateOf("") }

  // Rename Dialog
  renameTargetItem?.let { target ->
    AlertDialog(
      onDismissRequest = { renameTargetItem = null },
      title = {
        Text(
          text = AppStrings.renameTitle(language),
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = AppStrings.renamePrompt(language),
            style = MaterialTheme.typography.bodyMedium
          )
          OutlinedTextField(
            value = renameInputText,
            onValueChange = { renameInputText = it },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("rename_input_field")
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (renameInputText.isNotBlank()) {
              viewModel.renameHistoryItem(target.id, renameInputText)
            }
            renameTargetItem = null
          },
          modifier = Modifier.testTag("save_rename_button")
        ) {
          Text(AppStrings.renameConfirm(language))
        }
      },
      dismissButton = {
        TextButton(onClick = { renameTargetItem = null }) {
          Text(AppStrings.cancelButton(language))
        }
      }
    )
  }

  // Internal Video/Audio Player Modal (Requirement 1)
  activePlaybackItem?.let { item ->
    InternalMediaPlayerModal(
      item = item,
      language = language,
      onDismiss = { activePlaybackItem = null }
    )
  }

  if (showClearConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showClearConfirmDialog = false },
      title = {
        Text(
          text = AppStrings.clearConfirmTitle(language),
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(AppStrings.clearConfirmMessage(language))
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearAllHistory()
            showClearConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text(AppStrings.clearAllButton(language))
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirmDialog = false }) {
          Text(AppStrings.cancelButton(language))
        }
      }
    )
  }

  itemToDelete?.let { item ->
    AlertDialog(
      onDismissRequest = { itemToDelete = null },
      title = {
        Text(
          text = AppStrings.deleteItemTitle(language),
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(AppStrings.deleteItemMessage(language, item.title))
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteHistoryItem(item.id)
            itemToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text(AppStrings.deleteButton(language))
        }
      },
      dismissButton = {
        TextButton(onClick = { itemToDelete = null }) {
          Text(AppStrings.cancelButton(language))
        }
      }
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Row with Stats
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Text(
            text = AppStrings.historyTitle(language),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = AppStrings.totalDownloadsLabel(language, rawHistoryList.size),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Toggle View Mode (Grid / List)
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .clickable { isGridView = !isGridView }
              .testTag("toggle_history_view_mode_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                contentDescription = if (isGridView) AppStrings.listView(language) else AppStrings.gridView(language),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = if (isGridView) AppStrings.listView(language) else AppStrings.gridView(language),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          if (rawHistoryList.isNotEmpty()) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable { showClearConfirmDialog = true }
                .testTag("clear_all_history_button")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.DeleteOutline,
                  contentDescription = AppStrings.clearAllButton(language),
                  tint = MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = AppStrings.clearAllButton(language),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        }
      }
    }

    // Search & Sort Bar
    if (rawHistoryList.isNotEmpty()) {
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = {
            Text(
              text = AppStrings.searchVaultPlaceholder(language),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = if (language == AppLanguage.ARABIC) "مسح" else "Clear",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(14.dp),
          colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_search_field")
        )
      }

      // Sort & Platform Options Row
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Sort Options
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Sort,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = AppStrings.sortByTitle(language) + ":",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val sortItems = listOf(
              Triple(VaultSortCriteria.DATE_DESC, AppStrings.sortByDate(language), "date"),
              Triple(VaultSortCriteria.SIZE_DESC, AppStrings.sortBySize(language), "size"),
              Triple(VaultSortCriteria.NAME_ASC, AppStrings.sortByName(language), "name")
            )

            sortItems.forEach { (crit, label, tag) ->
              val isSelected = sortCriteria == crit
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { sortCriteria = crit }
                  .testTag("sort_by_${tag}_button")
              ) {
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
              }
            }
          }

          // Platform Filter Chips if multiple platforms exist
          val platforms = remember(rawHistoryList) { rawHistoryList.map { it.platform }.distinct() }
          if (platforms.size > 1) {
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selectedPlatformFilter == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .clickable { selectedPlatformFilter = null }
                  .testTag("filter_platform_all")
              ) {
                Text(
                  text = if (language == AppLanguage.ARABIC) "كل المنصات" else "All Platforms",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (selectedPlatformFilter == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              platforms.forEach { platform ->
                val isSelected = selectedPlatformFilter == platform
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                      selectedPlatformFilter = if (isSelected) null else platform
                    }
                    .testTag("filter_platform_${platform.name.lowercase()}")
                ) {
                  Text(
                    text = platform.getName(language),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Filter Chips Row
    if (rawHistoryList.isNotEmpty()) {
      item {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          FilterOptionPill(
            label = AppStrings.filterAll(language),
            count = rawHistoryList.size,
            isSelected = activeFilter == HistoryFilter.ALL,
            onClick = { viewModel.setHistoryFilter(HistoryFilter.ALL) },
            modifier = Modifier.weight(1f)
          )
          FilterOptionPill(
            label = AppStrings.filterVideo(language),
            count = rawHistoryList.count { !it.isAudioOnly },
            isSelected = activeFilter == HistoryFilter.VIDEO,
            onClick = { viewModel.setHistoryFilter(HistoryFilter.VIDEO) },
            modifier = Modifier.weight(1f)
          )
          FilterOptionPill(
            label = AppStrings.filterAudio(language),
            count = rawHistoryList.count { it.isAudioOnly },
            isSelected = activeFilter == HistoryFilter.AUDIO,
            onClick = { viewModel.setHistoryFilter(HistoryFilter.AUDIO) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // List of Downloads or Empty State
    if (historyList.isEmpty()) {
      item {
        EmptyStateCard(
          icon = Icons.Default.FolderOpen,
          title = AppStrings.historyEmptyTitle(language),
          description = AppStrings.historyEmptyDesc(language),
          actionButtonText = AppStrings.startDownloadingNow(language),
          onActionClick = onNavigateToHome
        )
      }
    } else if (isGridView) {
      val chunkedItems = historyList.chunked(2)
      items(
        items = chunkedItems,
        key = { chunk -> chunk.joinToString("-") { it.id } }
      ) { chunk ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          chunk.forEach { item ->
            Box(modifier = Modifier.weight(1f)) {
              CleanGridHistoryCard(
                item = item,
                language = language,
                onPlay = { activePlaybackItem = item },
                onPublishToGallery = { viewModel.publishToGallery(item.id) },
                onRename = {
                  renameTargetItem = item
                  renameInputText = item.title
                },
                onShare = {
                  shareMediaItem(
                    context = context,
                    item = item,
                    language = language,
                    onError = { errorMsg -> viewModel.showSnackbar(errorMsg) }
                  )
                },
                onOpenWith = {
                  openWithExternalApp(
                    context = context,
                    item = item,
                    language = language,
                    onError = { errorMsg -> viewModel.showSnackbar(errorMsg) }
                  )
                },
                onDelete = { itemToDelete = item }
              )
            }
          }
          if (chunk.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    } else {
      items(
        items = historyList,
        key = { it.id }
      ) { item ->
        CleanHistoryCard(
          item = item,
          language = language,
          onPlay = { activePlaybackItem = item },
          onPublishToGallery = { viewModel.publishToGallery(item.id) },
          onRename = {
            renameTargetItem = item
            renameInputText = item.title
          },
          onShare = {
            shareMediaItem(
              context = context,
              item = item,
              language = language,
              onError = { errorMsg -> viewModel.showSnackbar(errorMsg) }
            )
          },
          onOpenWith = {
            openWithExternalApp(
              context = context,
              item = item,
              language = language,
              onError = { errorMsg -> viewModel.showSnackbar(errorMsg) }
            )
          },
          onDelete = { itemToDelete = item }
        )
      }
    }
  }
}

@Composable
private fun FilterOptionPill(
  label: String,
  count: Int,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isSelected) {
    MaterialTheme.colorScheme.primary
  } else {
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
  }
  val textColor = if (isSelected) {
    MaterialTheme.colorScheme.onPrimary
  } else {
    MaterialTheme.colorScheme.onSurfaceVariant
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = textColor
      )
      Spacer(modifier = Modifier.width(6.dp))
      Surface(
        shape = CircleShape,
        color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.outlineVariant
      ) {
        Text(
          text = count.toString(),
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = textColor,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CleanHistoryCard(
  item: DownloadedItem,
  language: AppLanguage,
  onPlay: () -> Unit,
  onPublishToGallery: () -> Unit,
  onRename: () -> Unit,
  onShare: () -> Unit,
  onOpenWith: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("history_item_${item.id}")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Top row: Thumbnail + Details + Export Indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
      ) {
        // Thumbnail with overlay and Published badge
        Box(
          modifier = Modifier
            .size(86.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable { onPlay() }
        ) {
          if (!item.thumbnailUrl.isNullOrBlank()) {
            AsyncImage(
              model = item.thumbnailUrl,
              contentDescription = item.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
            )
          }

          // Center Play Icon on thumbnail
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .align(Alignment.Center)
              .size(34.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.92f))
          ) {
            Icon(
              imageVector = if (item.isAudioOnly) Icons.Default.Headphones else Icons.Default.PlayArrow,
              contentDescription = AppStrings.playButton(language),
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(18.dp)
            )
          }

          // Platform Dot (top start)
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(6.dp)
              .size(9.dp)
              .clip(CircleShape)
              .background(item.platform.brandColor)
          )

          // Duration badge if available (bottom end)
          if (!item.duration.isNullOrBlank() && item.duration != "00:00") {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color.Black.copy(alpha = 0.75f),
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
            ) {
              Text(
                text = item.duration,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
              )
            }
          }

          // Published to Gallery Indicator (Small green check inside small black circle)
          if (item.isExportedToGallery) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFF111111))
                .border(1.dp, Color(0xFF4CAF50), CircleShape)
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = AppStrings.publishedToGalleryBadge(language),
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }

        // Details: Title + Metadata Chips
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { onPlay() }
          )

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            // Quality chip
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = item.quality,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            // Size chip
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
              Text(
                text = item.size,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }

            // Duration chip if present
            if (!item.duration.isNullOrBlank() && item.duration != "00:00") {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
              ) {
                Text(
                  text = item.duration,
                  fontSize = 10.5.sp,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            // Date
            Text(
              text = item.date,
              fontSize = 10.5.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
              modifier = Modifier.align(Alignment.CenterVertically)
            )
          }

          // Gallery publication status notice
          if (item.isExportedToGallery) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(14.dp)
                  .clip(CircleShape)
                  .background(Color.Black)
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = Color(0xFF4CAF50),
                  modifier = Modifier.size(10.dp)
                )
              }
              Text(
                text = AppStrings.publishedToGalleryBadge(language),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
              )
            }
          }
        }
      }

      // Bottom Row: Separated, Clear Action Buttons (Play, Publish to Gallery, Share, Delete)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // 1. Play Button (Internal Media3 Player)
        Button(
          onClick = onPlay,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .testTag("play_button_${item.id}")
        ) {
          Icon(
            imageVector = if (item.isAudioOnly) Icons.Default.Headphones else Icons.Default.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = AppStrings.playButton(language),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
        }

        // 2. Transfer to Studio / Audio Files Button with Visual Confirmation
        if (item.isExportedToGallery) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.45f)),
            modifier = Modifier
              .height(40.dp)
              .testTag("transferred_badge_${item.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = AppStrings.transferredBadge(language),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
              )
            }
          }
        } else {
          OutlinedButton(
            onClick = onPublishToGallery,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier
              .height(40.dp)
              .testTag("publish_button_${item.id}")
          ) {
            Icon(
              imageVector = Icons.Default.CloudUpload,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = AppStrings.transferToGallery(language, item.isAudioOnly),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        // 3. Rename Button
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onRename() }
            .testTag("rename_button_${item.id}")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = AppStrings.renameTitle(language),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // 4. Share Button
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onShare() }
            .testTag("share_button_${item.id}")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = AppStrings.shareButton(language),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // 5. Open With Button (External App)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onOpenWith() }
            .testTag("open_with_button_${item.id}")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.OpenInNew,
              contentDescription = AppStrings.openWithAction(language),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // 4. Delete Button
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.25f)),
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onDelete() }
            .testTag("delete_button_${item.id}")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = AppStrings.deleteButton(language),
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun CleanGridHistoryCard(
  item: DownloadedItem,
  language: AppLanguage,
  onPlay: () -> Unit,
  onPublishToGallery: () -> Unit,
  onRename: () -> Unit,
  onShare: () -> Unit,
  onOpenWith: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("grid_history_item_${item.id}")
  ) {
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      // 1. Thumbnail Container (Height 115.dp)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(115.dp)
          .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable { onPlay() }
      ) {
        if (!item.thumbnailUrl.isNullOrBlank()) {
          coil.compose.AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.32f))
          )
        }

        // Center Play / Audio Icon
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .align(Alignment.Center)
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.92f))
        ) {
          Icon(
            imageVector = if (item.isAudioOnly) Icons.Default.Headphones else Icons.Default.PlayArrow,
            contentDescription = AppStrings.playButton(language),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Platform Dot badge (top start)
        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(8.dp)
            .size(10.dp)
            .clip(CircleShape)
            .background(item.platform.brandColor)
        )

        // Duration (bottom end)
        if (!item.duration.isNullOrBlank() && item.duration != "00:00") {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.Black.copy(alpha = 0.75f),
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(6.dp)
          ) {
            Text(
              text = item.duration,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White,
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
      }

      // 2. Info and Actions Body
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        // Quality and Size Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
          ) {
            Text(
              text = item.quality,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Text(
            text = item.size,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Transfer to Gallery / Audio or Transferred badge
        if (item.isExportedToGallery) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF10B981).copy(alpha = 0.12f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = AppStrings.publishedToGalleryBadge(language),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
              )
            }
          }
        } else {
          Button(
            onClick = onPublishToGallery,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer,
              contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CloudUpload,
              contentDescription = null,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = AppStrings.transferToGallery(language, item.isAudioOnly),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Bottom Action Icons: Share & Delete
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onRename,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = AppStrings.renameTitle(language),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(15.dp)
            )
          }

          IconButton(
            onClick = onShare,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Share,
              contentDescription = AppStrings.shareButton(language),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(15.dp)
            )
          }

          IconButton(
            onClick = onOpenWith,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.OpenInNew,
              contentDescription = AppStrings.openWithAction(language),
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(15.dp)
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = AppStrings.deleteButton(language),
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }
    }
  }
}

private fun shareMediaItem(
  context: Context,
  item: DownloadedItem,
  language: AppLanguage,
  onError: (String) -> Unit
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

private fun openWithExternalApp(
  context: Context,
  item: DownloadedItem,
  language: AppLanguage,
  onError: (String) -> Unit
) {
  val mime = item.mimeType ?: if (item.isAudioOnly) "audio/*" else "video/*"
  var openUri: Uri? = null

  if (!item.contentUri.isNullOrBlank()) {
    try {
      openUri = Uri.parse(item.contentUri)
    } catch (_: Exception) {}
  }

  if (openUri == null && !item.localFilePath.isNullOrBlank()) {
    val file = File(item.localFilePath)
    if (file.exists() && file.length() > 0) {
      try {
        openUri = FileProvider.getUriForFile(
          context,
          "${context.packageName}.fileprovider",
          file
        )
      } catch (_: Exception) {
        openUri = Uri.fromFile(file)
      }
    }
  }

  if (openUri == null) {
    onError(AppStrings.mediaFileNotFound(language))
    return
  }

  val viewIntent = Intent(Intent.ACTION_VIEW).apply {
    setDataAndType(openUri, mime)
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  }

  try {
    val chooser = Intent.createChooser(viewIntent, item.title).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooser)
  } catch (_: Exception) {
    onError("تعذر فتح التطبيق الخارجي")
  }
}

private fun parseSizeToBytes(sizeStr: String): Long {
  try {
    val clean = sizeStr.trim()
    val parts = clean.split(" ")
    if (parts.size >= 2) {
      val num = parts[0].toDoubleOrNull() ?: return 0L
      val unit = parts[1].uppercase()
      return when {
        unit.startsWith("G") -> (num * 1024 * 1024 * 1024).toLong()
        unit.startsWith("M") -> (num * 1024 * 1024).toLong()
        unit.startsWith("K") -> (num * 1024).toLong()
        else -> num.toLong()
      }
    }
  } catch (_: Exception) {}
  return 0L
}
