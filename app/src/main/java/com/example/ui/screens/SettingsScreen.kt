package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.PlatformType
import com.example.ui.components.PlatformBadgeChip
import com.example.ui.theme.ThemeMode
import com.example.viewmodel.BackendConnectionStatus
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
  viewModel: MainViewModel,
  onNavigateToDiagnostics: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val themeMode by viewModel.themeMode.collectAsState()
  val language by viewModel.language.collectAsState()
  val cacheSize by viewModel.cacheSize.collectAsState()
  val backendStatus by viewModel.backendStatus.collectAsState()
  val fontScale by viewModel.fontSizeScale.collectAsState()
  val wifiOnly by viewModel.wifiOnly.collectAsState()
  val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
  val autoAnalyzeOnPaste by viewModel.autoAnalyzeOnPaste.collectAsState()
  val autoExportToGallery by viewModel.autoExportToGallery.collectAsState()
  val defaultQuality by viewModel.defaultQuality.collectAsState()
  val defaultPlaybackSpeed by viewModel.defaultPlaybackSpeed.collectAsState()
  val hapticFeedbackEnabled by viewModel.hapticFeedbackEnabled.collectAsState()

  // Interactive Dialog & Expand States
  var showClearCacheDialog by remember { mutableStateOf(false) }
  var showClearHistoryDialog by remember { mutableStateOf(false) }
  var showDefaultQualityDialog by remember { mutableStateOf(false) }
  var showPlaybackSpeedDialog by remember { mutableStateOf(false) }
  var showPrivacyDialog by remember { mutableStateOf(false) }
  var isAppearanceExpanded by remember { mutableStateOf(false) }
  var isFontSizeExpanded by remember { mutableStateOf(false) }
  var isLanguageExpanded by remember { mutableStateOf(false) }
  var isDownloadSettingsExpanded by remember { mutableStateOf(true) }
  var isCacheExpanded by remember { mutableStateOf(false) }
  var isAboutExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 18.dp),
    contentPadding = PaddingValues(top = 18.dp, bottom = 40.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Title with Collapse/Expand All action
    item {
      val allExpanded = isAppearanceExpanded && isFontSizeExpanded && isLanguageExpanded &&
          isDownloadSettingsExpanded && isCacheExpanded && isAboutExpanded

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
          ) {
            Icon(
              imageVector = Icons.Default.Menu,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
          }
          Column {
            Text(
              text = AppStrings.settingsTitle(language),
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = AppStrings.settingsSubtitle(language),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable {
              val newState = !allExpanded
              isAppearanceExpanded = newState
              isFontSizeExpanded = newState
              isLanguageExpanded = newState
              isDownloadSettingsExpanded = newState
              isCacheExpanded = newState
              isAboutExpanded = newState
            }
            .testTag("toggle_all_settings_button")
        ) {
          Text(
            text = if (allExpanded) {
              if (language == AppLanguage.ARABIC) "طي الكل" else if (language == AppLanguage.FRENCH) "Tout replier" else "Collapse All"
            } else {
              if (language == AppLanguage.ARABIC) "توسيع الكل" else if (language == AppLanguage.FRENCH) "Tout déplier" else "Expand All"
            },
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    // 1. Appearance (المظهر والسمة)
    item {
      val currentThemeSubtitle = when (themeMode) {
        ThemeMode.SYSTEM -> AppStrings.themeSystem(language)
        ThemeMode.LIGHT -> AppStrings.themeLight(language)
        ThemeMode.DARK -> AppStrings.themeDark(language)
      }

      ExpandableSettingCard(
        title = AppStrings.appearanceSection(language),
        subtitle = currentThemeSubtitle,
        icon = Icons.Default.BrightnessAuto,
        isExpanded = isAppearanceExpanded,
        onToggleExpand = { isAppearanceExpanded = !isAppearanceExpanded },
        modifier = Modifier.testTag("setting_card_appearance")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text(
            text = AppStrings.appearanceDesc(language),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            ThemeChoiceCard(
              title = AppStrings.themeSystem(language),
              subtitle = AppStrings.themeSystemSub(language),
              icon = Icons.Default.BrightnessAuto,
              isSelected = themeMode == ThemeMode.SYSTEM,
              onClick = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
              modifier = Modifier.weight(1f)
            )
            ThemeChoiceCard(
              title = AppStrings.themeLight(language),
              subtitle = AppStrings.themeLightSub(language),
              icon = Icons.Default.LightMode,
              isSelected = themeMode == ThemeMode.LIGHT,
              onClick = { viewModel.setThemeMode(ThemeMode.LIGHT) },
              modifier = Modifier.weight(1f)
            )
            ThemeChoiceCard(
              title = AppStrings.themeDark(language),
              subtitle = AppStrings.themeDarkSub(language),
              icon = Icons.Default.DarkMode,
              isSelected = themeMode == ThemeMode.DARK,
              onClick = { viewModel.setThemeMode(ThemeMode.DARK) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // 2. Font Size (حجم الخط والنصوص)
    item {
      val scaleLabel = when {
        fontScale < 0.95f -> AppStrings.fontSizeSmall(language)
        fontScale < 1.10f -> AppStrings.fontSizeNormal(language)
        fontScale < 1.25f -> AppStrings.fontSizeLarge(language)
        else -> AppStrings.fontSizeHuge(language)
      }

      ExpandableSettingCard(
        title = AppStrings.fontSizeTitle(language),
        subtitle = "$scaleLabel (${(fontScale * 100).toInt()}%)",
        icon = Icons.Default.FormatSize,
        isExpanded = isFontSizeExpanded,
        onToggleExpand = { isFontSizeExpanded = !isFontSizeExpanded },
        modifier = Modifier.testTag("setting_card_font_size")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text(
            text = AppStrings.fontSizeSubtitle(language),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Slider(
            value = fontScale,
            onValueChange = { viewModel.setFontSizeScale(it) },
            valueRange = 0.85f..1.35f,
            steps = 4,
            colors = SliderDefaults.colors(
              thumbColor = MaterialTheme.colorScheme.primary,
              activeTrackColor = MaterialTheme.colorScheme.primary,
              inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("font_size_slider")
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = AppStrings.fontSizeSmall(language),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = AppStrings.fontSizeNormal(language),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = AppStrings.fontSizeLarge(language),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = AppStrings.fontSizeHuge(language),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Live Text Preview Card
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = if (language == AppLanguage.ARABIC) "معاينة حجم الخط:" else if (language == AppLanguage.FRENCH) "Aperçu de la taille :" else "Font Size Preview:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = if (language == AppLanguage.ARABIC) "تطبيق خاطف لتحميل وتشغيل الفيديوهات والصوتيات بأعلى سرعة وجودة." else if (language == AppLanguage.FRENCH) "Téléchargement ultra rapide de vidéos et audio avec Khatif." else "Ultra fast video and audio downloader with Khatif.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    // 3. Language (لغة التطبيق)
    item {
      ExpandableSettingCard(
        title = AppStrings.languageSection(language),
        subtitle = language.displayName,
        icon = Icons.Default.Language,
        isExpanded = isLanguageExpanded,
        onToggleExpand = { isLanguageExpanded = !isLanguageExpanded },
        modifier = Modifier.testTag("setting_card_language")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text(
            text = AppStrings.languagePrompt(language),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            AppLanguage.values().forEach { langOption ->
              val isSelected = language == langOption
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(
                  if (isSelected) 1.5.dp else 1.dp,
                  if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { viewModel.setLanguage(langOption) }
                  .testTag("lang_button_${langOption.code}")
              ) {
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
                  modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
                ) {
                  Text(
                    text = langOption.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = if (langOption.isRtl) "RTL" else "LTR",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
    }

    // 4. Download Preferences & Automation (خيارات التنزيل والأتمتة)
    item {
      ExpandableSettingCard(
        title = AppStrings.downloadPreferencesSection(language),
        subtitle = AppStrings.downloadPreferencesSubtitle(language),
        icon = Icons.Default.CloudDownload,
        isExpanded = isDownloadSettingsExpanded,
        onToggleExpand = { isDownloadSettingsExpanded = !isDownloadSettingsExpanded },
        modifier = Modifier.testTag("setting_card_download_preferences")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Auto-analyze on Paste
          SettingSwitchRow(
            icon = Icons.Default.ContentPaste,
            title = AppStrings.autoAnalyzeOnPasteTitle(language),
            subtitle = AppStrings.autoAnalyzeOnPasteSubtitle(language),
            checked = autoAnalyzeOnPaste,
            onCheckedChange = { viewModel.setAutoAnalyzeOnPaste(it) }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          // Auto-Save to Gallery
          SettingSwitchRow(
            icon = Icons.Default.Folder,
            title = AppStrings.autoExportGalleryTitle(language),
            subtitle = AppStrings.autoExportGallerySubtitle(language),
            checked = autoExportToGallery,
            onCheckedChange = { viewModel.setAutoExportToGallery(it) }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          // Default Download Quality
          SettingDetailRow(
            icon = Icons.Default.HighQuality,
            title = AppStrings.defaultQualityTitle(language),
            subtitle = defaultQuality,
            action = if (language == AppLanguage.ARABIC) "تحديد" else if (language == AppLanguage.FRENCH) "Choisir" else "Select",
            onClick = { showDefaultQualityDialog = true }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          // Default Playback Speed
          SettingDetailRow(
            icon = Icons.Default.Speed,
            title = AppStrings.defaultPlaybackSpeedTitle(language),
            subtitle = "${defaultPlaybackSpeed}x",
            action = if (language == AppLanguage.ARABIC) "تحديد" else if (language == AppLanguage.FRENCH) "Choisir" else "Select",
            onClick = { showPlaybackSpeedDialog = true }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          // Wi-Fi Only Download
          SettingSwitchRow(
            icon = Icons.Default.Wifi,
            title = AppStrings.wifiOnlyTitle(language),
            subtitle = AppStrings.wifiOnlySubtitle(language),
            checked = wifiOnly,
            onCheckedChange = { viewModel.setWifiOnly(it) }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          // Download Notifications
          SettingSwitchRow(
            icon = Icons.Default.Notifications,
            title = AppStrings.notificationsTitle(language),
            subtitle = AppStrings.notificationsSubtitle(language),
            checked = notificationsEnabled,
            onCheckedChange = { viewModel.setNotificationsEnabled(it) }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          // Haptic Touch Feedback
          SettingSwitchRow(
            icon = Icons.Default.Bolt,
            title = AppStrings.hapticFeedbackTitle(language),
            subtitle = AppStrings.hapticFeedbackSubtitle(language),
            checked = hapticFeedbackEnabled,
            onCheckedChange = { viewModel.setHapticFeedbackEnabled(it) }
          )
        }
      }
    }

    // 5. Cache & Storage (الذاكرة المؤقتة والتخزين)
    item {
      ExpandableSettingCard(
        title = AppStrings.cacheSection(language),
        subtitle = AppStrings.cacheOccupied(language, cacheSize),
        icon = Icons.Default.CleaningServices,
        isExpanded = isCacheExpanded,
        onToggleExpand = { isCacheExpanded = !isCacheExpanded },
        modifier = Modifier.testTag("setting_card_cache")
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          SettingDetailRow(
            icon = Icons.Default.CleaningServices,
            title = AppStrings.clearCacheTitle(language),
            subtitle = AppStrings.cacheOccupied(language, cacheSize),
            action = AppStrings.cleanButton(language),
            onClick = { showClearCacheDialog = true }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

          SettingDetailRow(
            icon = Icons.Default.Delete,
            title = AppStrings.clearAllHistoryTitle(language),
            subtitle = AppStrings.clearAllHistorySubtitle(language),
            action = if (language == AppLanguage.ARABIC) "مسح" else if (language == AppLanguage.FRENCH) "Vider" else "Clear",
            onClick = { showClearHistoryDialog = true }
          )
        }
      }
    }

    // 5. About Section (حول) - Expandable Accordion
    item {
      ExpandableSettingCard(
        title = AppStrings.aboutSection(language),
        subtitle = if (isAboutExpanded) AppStrings.tapToClose(language) else AppStrings.tapToViewDetails(language),
        icon = Icons.Default.Info,
        isExpanded = isAboutExpanded,
        onToggleExpand = { isAboutExpanded = !isAboutExpanded },
        modifier = Modifier.testTag("setting_card_about")
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // 1. الإصدار (Version)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
              Icon(
                imageVector = Icons.Default.Diamond,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = AppStrings.versionLabel(language),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = AppStrings.aboutVersion(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          // Specs breakdown
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = AppStrings.buildTypeLabel(language),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = AppStrings.buildTypeValue(language),
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = AppStrings.engineLabel(language),
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = AppStrings.engineValue(language),
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

          // 2. مباشرة تحتها: السياسات والشروط
          SettingDetailRow(
            icon = Icons.Default.PrivacyTip,
            title = AppStrings.privacyPolicyTitle(language),
            subtitle = AppStrings.privacyPolicySubtitle(language),
            action = AppStrings.viewButton(language),
            onClick = { showPrivacyDialog = true }
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

          // 3. تشخيص النظام والخدمة
          SettingDetailRow(
            icon = Icons.Default.Cloud,
            title = AppStrings.diagnosticsTitle(language),
            subtitle = when (backendStatus) {
              BackendConnectionStatus.ONLINE -> if (language == AppLanguage.ARABIC) "الخدمة متصلة بنجاح ✓" else "Service Connected ✓"
              BackendConnectionStatus.CHECKING -> if (language == AppLanguage.ARABIC) "جاري التحقق من الخدمة..." else "Checking Service..."
              BackendConnectionStatus.OFFLINE -> if (language == AppLanguage.ARABIC) "الخدمة غير متصلة" else "Service Offline"
            },
            action = if (language == AppLanguage.ARABIC) "تشخيص" else "Diagnose",
            onClick = { onNavigateToDiagnostics() }
          )
        }
      }
    }
  }

  // --- INTERACTIVE DIALOG: Clear Cache Confirmation ---
  if (showClearCacheDialog) {
    AlertDialog(
      onDismissRequest = { showClearCacheDialog = false },
      icon = {
        Icon(
          imageVector = Icons.Default.CleaningServices,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      },
      title = {
        Text(
          text = AppStrings.clearCacheDialogTitle(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(
          text = if (cacheSize == "0.0 MB") {
            AppStrings.clearCacheEmptyPrompt(language)
          } else {
            AppStrings.clearCacheConfirmPrompt(language, cacheSize)
          },
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearCache()
            showClearCacheDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text(AppStrings.clearNowButton(language), fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearCacheDialog = false }) {
          Text(AppStrings.cancelButton(language), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    )
  }

  // --- INTERACTIVE DIALOG 4: Privacy Policy & Terms of Service ---
  if (showPrivacyDialog) {
    AlertDialog(
      onDismissRequest = { showPrivacyDialog = false },
      icon = {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
          Icon(
            imageVector = Icons.Default.PrivacyTip,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
          )
        }
      },
      title = {
        Text(
          text = AppStrings.privacyDialogTitle(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
      },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Card 1: Data Protection & Privacy
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = AppStrings.privacyDataSectionTitle(language),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = AppStrings.privacyDataSectionContent(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
              )
            }
          }

          // Card 2: Storage & Permissions
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = AppStrings.privacyStorageSectionTitle(language),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = AppStrings.privacyStorageSectionContent(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
              )
            }
          }

          // Card 3: Terms of Use
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text(
                text = AppStrings.privacyTermsSectionTitle(language),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = AppStrings.privacyTermsSectionContent(language),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { showPrivacyDialog = false },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text(
            text = AppStrings.privacyAgreeButton(language),
            fontWeight = FontWeight.Bold
          )
        }
      }
    )
  }

  // --- INTERACTIVE DIALOG: Default Download Quality Selector ---
  if (showDefaultQualityDialog) {
    val qualityOptions = listOf(
      "أفضل جودة (تلقائي / Best)" to "أفضل جودة",
      "1080p Full HD" to "1080p",
      "720p HD" to "720p",
      "480p SD" to "480p",
      "360p Standard" to "360p",
      "صوت فقط (Audio Only)" to "صوت فقط"
    )

    AlertDialog(
      onDismissRequest = { showDefaultQualityDialog = false },
      icon = {
        Icon(
          imageVector = Icons.Default.HighQuality,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      },
      title = {
        Text(
          text = AppStrings.defaultQualityTitle(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = AppStrings.defaultQualitySubtitle(language),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          qualityOptions.forEach { (label, key) ->
            val isSelected = defaultQuality.contains(key, ignoreCase = true)
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  viewModel.setDefaultQuality(label)
                  showDefaultQualityDialog = false
                }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = {
                    viewModel.setDefaultQuality(label)
                    showDefaultQualityDialog = false
                  },
                  colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                  text = label,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showDefaultQualityDialog = false }) {
          Text(AppStrings.cancelButton(language))
        }
      }
    )
  }

  // --- INTERACTIVE DIALOG: Default Playback Speed Selector ---
  if (showPlaybackSpeedDialog) {
    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)
    AlertDialog(
      onDismissRequest = { showPlaybackSpeedDialog = false },
      icon = {
        Icon(
          imageVector = Icons.Default.Speed,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(32.dp)
        )
      },
      title = {
        Text(
          text = AppStrings.defaultPlaybackSpeedTitle(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = AppStrings.defaultPlaybackSpeedSubtitle(language),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          speedOptions.forEach { speed ->
            val isSelected = defaultPlaybackSpeed == speed
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable {
                  viewModel.setDefaultPlaybackSpeed(speed)
                  showPlaybackSpeedDialog = false
                }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
              ) {
                RadioButton(
                  selected = isSelected,
                  onClick = {
                    viewModel.setDefaultPlaybackSpeed(speed)
                    showPlaybackSpeedDialog = false
                  },
                  colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                )
                Text(
                  text = "${speed}x" + if (speed == 1.0f) " (عادي / Normal)" else "",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showPlaybackSpeedDialog = false }) {
          Text(AppStrings.cancelButton(language))
        }
      }
    )
  }

  // --- INTERACTIVE DIALOG: Clear All History Confirmation ---
  if (showClearHistoryDialog) {
    AlertDialog(
      onDismissRequest = { showClearHistoryDialog = false },
      icon = {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
          modifier = Modifier.size(32.dp)
        )
      },
      title = {
        Text(
          text = AppStrings.clearAllHistoryTitle(language),
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Text(
          text = AppStrings.clearAllHistoryConfirm(language),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearAllHistory(deleteFiles = true)
            showClearHistoryDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text(
            text = if (language == AppLanguage.ARABIC) "مسح وحذف الكل" else if (language == AppLanguage.FRENCH) "Tout supprimer" else "Delete All",
            fontWeight = FontWeight.Bold
          )
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearHistoryDialog = false }) {
          Text(AppStrings.cancelButton(language), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    )
  }
}

@Composable
private fun ExpandableSettingCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  modifier: Modifier = Modifier,
  iconContainerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
  iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimary,
  content: @Composable () -> Unit
) {
  val rotationState by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    label = "expandRotation"
  )

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier
      .fillMaxWidth()
      .animateContentSize()
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header Row - Clickable to expand/collapse
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .clickable { onToggleExpand() }
          .padding(vertical = 4.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.weight(1f)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(iconContainerColor)
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = iconTint,
              modifier = Modifier.size(24.dp)
            )
          }
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = subtitle,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Icon(
          imageVector = Icons.Default.KeyboardArrowDown,
          contentDescription = if (isExpanded) "Collapse" else "Expand",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier
            .size(28.dp)
            .rotate(rotationState)
        )
      }

      AnimatedVisibility(
        visible = isExpanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          content()
        }
      }
    }
  }
}

@Composable
private fun UnifiedStepRow(text: String) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    border = androidx.compose.foundation.BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface,
      lineHeight = 20.sp,
      modifier = Modifier.padding(12.dp)
    )
  }
}

@Composable
private fun ThemeChoiceCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val containerColor = if (isSelected) {
    MaterialTheme.colorScheme.primaryContainer
  } else {
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
  }
  val strokeColor = if (isSelected) {
    MaterialTheme.colorScheme.primary
  } else {
    MaterialTheme.colorScheme.outlineVariant
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = containerColor,
    border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.8.dp else 1.dp, strokeColor),
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() }
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun SettingDetailRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  action: String,
  onClick: (() -> Unit)? = null
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
      .padding(vertical = 4.dp)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.weight(1f)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.primaryContainer,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
      modifier = Modifier.clip(RoundedCornerShape(8.dp))
    ) {
      Text(
        text = action,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
      )
    }
  }
}

@Composable
private fun SettingSwitchRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.weight(1f)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(18.dp)
        )
      }
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
        checkedTrackColor = MaterialTheme.colorScheme.primary
      )
    )
  }
}
