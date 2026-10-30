package com.tasbeeh.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tasbeeh.app.data.TasbeehViewModel

object Routes {
    const val COUNTER = "counter"
    const val DHIKR_SELECT = "dhikr_select"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(viewModel: TasbeehViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val showTargetReached by viewModel.showTargetReached.collectAsState()

    NavHost(navController = navController, startDestination = Routes.COUNTER) {
        composable(Routes.COUNTER) {
            CounterScreen(
                state = state,
                showTargetReached = showTargetReached,
                onSetTarget = { viewModel.setTarget(it) },
                onIncrement = { viewModel.increment() },
                onUndo = { viewModel.undoLast() },
                onReset = { viewModel.reset() },
                onDismissTargetReached = { viewModel.dismissTargetReached() },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNavigateToDhikrSelect = { navController.navigate(Routes.DHIKR_SELECT) }
            )
        }
        composable(Routes.DHIKR_SELECT) {
            DhikrSelectScreen(
                state = state,
                onSelectDhikr = { viewModel.selectDhikr(it) },
                onSetCustomDhikr = { viewModel.setCustomDhikr(it) },
                onSetTarget = { viewModel.setTarget(it) },
                onSetCustomTarget = { viewModel.setCustomTarget(it) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                sessions = sessions,
                onClearHistory = { viewModel.clearHistory() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                state = state,
                onVibrationToggle = { viewModel.setVibration(it) },
                onSoundToggle = { viewModel.setSound(it) },
                onThemeToggle = { viewModel.setTheme(it) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
