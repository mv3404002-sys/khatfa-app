package com.example.ui.screens

import android.os.StatFs
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.viewmodel.BackendConnectionStatus
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
  viewModel: MainViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val language by viewModel.language.collectAsState()
  val backendStatus by viewModel.backendStatus.collectAsState()
  val lastCheckTime by viewModel.lastBackendCheckTime.collectAsState()
  val context = LocalContext.current

  // Calculate real device storage statistics safely
  val storageStats = remember {
    try {
      val statFs = StatFs(context.filesDir.path)
      val available = statFs.availableBytes
      val total = statFs.totalBytes
      val used = (total - available).coerceAtLeast(0L)
      val usedPercent = if (total > 0L) (used.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
      Triple(available, total, usedPercent)
    } catch (_: Exception) {
      Triple(0L, 0L, 0f)
    }
  }

  val availableGbFormatted = remember(storageStats.first) {
    String.format(java.util.Locale.US, "%.1f GB", storageStats.first.toDouble() / (1024 * 1024 * 1024))
  }
  val totalGbFormatted = remember(storageStats.second) {
    String.format(java.util.Locale.US, "%.1f GB", storageStats.second.toDouble() / (1024 * 1024 * 1024))
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = AppStrings.diagnosticsTitle(language),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("diagnostics_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = if (language == AppLanguage.ARABIC) "رجوع" else "Back"
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.background,
          titleContentColor = MaterialTheme.colorScheme.onBackground,
          navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier.fillMaxSize()
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 18.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Connection Status Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("diagnostics_connection_card")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(
                    when (backendStatus) {
                      BackendConnectionStatus.ONLINE -> Color(0xFF10B981).copy(alpha = 0.15f)
                      BackendConnectionStatus.CHECKING -> MaterialTheme.colorScheme.primaryContainer
                      BackendConnectionStatus.OFFLINE -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    }
                  )
              ) {
                Icon(
                  imageVector = when (backendStatus) {
                    BackendConnectionStatus.ONLINE -> Icons.Default.CloudDone
                    BackendConnectionStatus.CHECKING -> Icons.Default.Sync
                    BackendConnectionStatus.OFFLINE -> Icons.Default.ErrorOutline
                  },
                  contentDescription = null,
                  tint = when (backendStatus) {
                    BackendConnectionStatus.ONLINE -> Color(0xFF10B981)
                    BackendConnectionStatus.CHECKING -> MaterialTheme.colorScheme.primary
                    BackendConnectionStatus.OFFLINE -> MaterialTheme.colorScheme.error
                  },
                  modifier = Modifier.size(24.dp)
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = AppStrings.serviceStatusLabel(language),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = when (backendStatus) {
                    BackendConnectionStatus.ONLINE -> if (language == AppLanguage.ARABIC) "متصل بالخدمة بنجاح" else "Connected to Service"
                    BackendConnectionStatus.CHECKING -> if (language == AppLanguage.ARABIC) "جاري التحقق من الخدمة..." else "Checking Service..."
                    BackendConnectionStatus.OFFLINE -> if (language == AppLanguage.ARABIC) "غير متصل بالخدمة" else "Disconnected from Service"
                  },
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = when (backendStatus) {
                    BackendConnectionStatus.ONLINE -> Color(0xFF10B981)
                    BackendConnectionStatus.CHECKING -> MaterialTheme.colorScheme.primary
                    BackendConnectionStatus.OFFLINE -> MaterialTheme.colorScheme.error
                  }
                )
              }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Last check time row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = AppStrings.lastCheckTimeLabel(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = lastCheckTime ?: (if (language == AppLanguage.ARABIC) "قبل قليل" else "Moments ago"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            // Action button to re-check
            Button(
              onClick = { viewModel.checkBackendHealth() },
              enabled = backendStatus != BackendConnectionStatus.CHECKING,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
              ),
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("run_diagnostics_check_button")
            ) {
              if (backendStatus == BackendConnectionStatus.CHECKING) {
                CircularProgressIndicator(
                  color = MaterialTheme.colorScheme.onPrimary,
                  strokeWidth = 2.dp,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
              } else {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
              }
              Text(
                text = AppStrings.runCheckButton(language),
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // 2. Storage Status Card
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("diagnostics_storage_card")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer)
              ) {
                Icon(
                  imageVector = Icons.Default.Storage,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(24.dp)
                )
              }

              Column {
                Text(
                  text = AppStrings.storageStatusLabel(language),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "$availableGbFormatted ${AppStrings.storageAvailable(language)}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            // Progress bar
            LinearProgressIndicator(
              progress = { storageStats.third },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = AppStrings.storageAvailable(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = availableGbFormatted,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = AppStrings.storageTotal(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = totalGbFormatted,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}
