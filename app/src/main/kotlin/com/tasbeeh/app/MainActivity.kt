package com.tasbeeh.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tasbeeh.app.data.TasbeehViewModel
import com.tasbeeh.app.ui.screens.AppNavigation
import com.tasbeeh.app.ui.theme.TasbeehTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: TasbeehViewModel = viewModel()
            val state by vm.state.collectAsState()
            TasbeehTheme(darkTheme = state.isDarkTheme) {
                AppNavigation(viewModel = vm)
            }
        }
    }
}
