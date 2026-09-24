package com.example.ui.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.viewmodel.MainViewModel

@Composable
fun MainAppContainer(
  viewModel: MainViewModel,
  modifier: Modifier = Modifier
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
  val snackbarHostState = remember { SnackbarHostState() }
  val snackbarMessage by viewModel.snackbarMessage.collectAsState()
  val language by viewModel.language.collectAsState()

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.dismissSnackbar()
    }
  }

  // Auto-navigate to History tab when download completes
  LaunchedEffect(Unit) {
    viewModel.navigateToHistoryEvent.collect {
      if (currentRoute != Screen.History.route) {
        navController.navigate(Screen.History.route) {
          popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
          }
          launchSingleTop = true
          restoreState = true
        }
      }
    }
  }

  val layoutDirection = if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    Scaffold(
      modifier = modifier.fillMaxSize(),
      snackbarHost = { SnackbarHost(snackbarHostState) },
      bottomBar = {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 6.dp,
          modifier = Modifier
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            .testTag("bottom_navigation_bar")
        ) {
          navigationTabs.forEach { screen ->
            val isSelected = currentRoute == screen.route
            val itemTitle = screen.getTitle(language)
            NavigationBarItem(
              icon = {
                Icon(
                  imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                  contentDescription = itemTitle
                )
              },
              label = {
                Text(
                  text = itemTitle,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              selected = isSelected,
              onClick = {
                if (currentRoute != screen.route) {
                  navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.testTag("nav_item_${screen.route}")
            )
          }
        }
      }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        composable(Screen.Home.route) {
          HomeScreen(
            viewModel = viewModel,
            onNavigateToHistory = {
              navController.navigate(Screen.History.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                  saveState = true
                }
                launchSingleTop = true
                restoreState = true
              }
            }
          )
        }
        composable(Screen.History.route) {
          HistoryScreen(
            viewModel = viewModel,
            onNavigateToHome = {
              navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                  saveState = true
                }
                launchSingleTop = true
                restoreState = true
              }
            }
          )
        }
        composable(Screen.Settings.route) {
          SettingsScreen(
            viewModel = viewModel
          )
        }
      }
    }
  }
}
