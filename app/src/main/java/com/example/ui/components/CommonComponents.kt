package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxSize
import coil.compose.AsyncImage
import com.example.localization.AppLanguage
import com.example.localization.AppStrings
import com.example.model.PlatformType
import com.example.model.VideoQualityOption

@Composable
fun PlatformBadgeChip(
  platform: PlatformType,
  isSelected: Boolean = false,
  language: AppLanguage = AppLanguage.ARABIC,
  onClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  val surfaceColor = if (isSelected) {
    MaterialTheme.colorScheme.primaryContainer
  } else {
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
  }
  val borderColor = if (isSelected) {
    MaterialTheme.colorScheme.primary
  } else {
    MaterialTheme.colorScheme.outlineVariant
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = surfaceColor,
    border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
      .testTag("platform_chip_${platform.name.lowercase()}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(platform.brandColor)
      )
      Text(
        text = platform.getName(language),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
      )
      if (platform.isUnderMaintenance) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
          border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
        ) {
          Text(
            text = AppStrings.platformMaintenanceBadge(language),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}

@Composable
fun VideoThumbnailView(
  title: String,
  duration: String,
  platform: PlatformType,
  thumbnailUrl: String? = null,
  isAudioOnly: Boolean = false,
  modifier: Modifier = Modifier
) {
  val gradientBrush = Brush.linearGradient(
    listOf(
      Color(0xFF221A15),
      Color(0xFF382920),
      Color(0xFF4E372A)
    )
  )

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .background(gradientBrush)
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
  ) {
    if (!thumbnailUrl.isNullOrBlank()) {
      AsyncImage(
        model = thumbnailUrl,
        contentDescription = title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
      // Dark gradient overlay for text readability
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.45f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.65f)
              )
            )
          )
      )
    }

    // Platform Tag
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color.Black.copy(alpha = 0.7f),
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(10.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(platform.brandColor)
        )
        Text(
          text = platform.englishName,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Duration Tag
    Surface(
      shape = RoundedCornerShape(6.dp),
      color = Color.Black.copy(alpha = 0.75f),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(10.dp)
    ) {
      Text(
        text = duration,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
      )
    }

    // Centered Play/Audio Badge
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .align(Alignment.Center)
        .size(54.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primary)
    ) {
      Icon(
        imageVector = if (isAudioOnly) Icons.Default.Headphones else Icons.Default.PlayArrow,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.size(28.dp)
      )
    }
  }
}

@Composable
fun QualitySelectorCard(
  option: VideoQualityOption,
  isSelected: Boolean,
  language: AppLanguage = AppLanguage.ARABIC,
  onSelect: () -> Unit,
  modifier: Modifier = Modifier
) {
  val backgroundColor = if (isSelected) {
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
    color = backgroundColor,
    border = androidx.compose.foundation.BorderStroke(if (isSelected) 1.8.dp else 1.dp, strokeColor),
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable { onSelect() }
      .testTag("quality_option_${option.id}")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
              if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
          Icon(
            imageVector = if (option.isAudioOnly) Icons.Default.Headphones else Icons.Default.Videocam,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = option.label,
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ) {
              Text(
                text = option.badge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = AppStrings.sizeLabel(language, option.size),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
      if (isSelected) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun EmptyStateCard(
  icon: ImageVector,
  title: String,
  description: String,
  actionButtonText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(22.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(26.dp)
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(34.dp)
        )
      }
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 22.sp
      )
      if (actionButtonText != null && onActionClick != null) {
        Spacer(modifier = Modifier.height(18.dp))
        Button(
          onClick = onActionClick,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          )
        ) {
          Text(
            text = actionButtonText,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
