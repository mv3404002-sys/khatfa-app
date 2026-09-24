package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.localization.AppLanguage
import com.example.localization.AppStrings

sealed class Screen(
  val route: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  abstract fun getTitle(lang: AppLanguage): String

  object Home : Screen(
    route = "home",
    selectedIcon = Icons.Filled.Bolt,
    unselectedIcon = Icons.Outlined.Bolt
  ) {
    override fun getTitle(lang: AppLanguage) = AppStrings.homeTab(lang)
  }

  object History : Screen(
    route = "history",
    selectedIcon = Icons.Filled.Folder,
    unselectedIcon = Icons.Outlined.Folder
  ) {
    override fun getTitle(lang: AppLanguage) = AppStrings.historyTab(lang)
  }

  object Settings : Screen(
    route = "settings",
    selectedIcon = Icons.Filled.Menu,
    unselectedIcon = Icons.Outlined.Menu
  ) {
    override fun getTitle(lang: AppLanguage) = AppStrings.settingsTab(lang)
  }
}

val navigationTabs = listOf(Screen.Home, Screen.History, Screen.Settings)
