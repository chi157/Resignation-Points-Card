package com.chi157.resignationpointscard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object CompanyName : Screen("company_name")
    object ThemeSelection : Screen("theme_selection")
    object Main : Screen("main")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // 等待數據加載
    if (isLoading) {
        // 可以顯示載入畫面
        return
    }
    
    // 決定起始畫面
    val startDestination = when {
        settings?.isOnboardingCompleted == true -> Screen.Main.route
        settings?.companyName?.isNotEmpty() == true -> Screen.ThemeSelection.route
        else -> Screen.CompanyName.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 公司名稱畫面
        composable(Screen.CompanyName.route) {
            SetupScreen(
                onConfirm = { companyName ->
                    viewModel.saveCompanyName(companyName)
                    navController.navigate(Screen.ThemeSelection.route) {
                        popUpTo(Screen.CompanyName.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 主題選擇畫面
        composable(Screen.ThemeSelection.route) {
            ThemeSelectionScreen(
                onThemeSelected = { theme ->
                    viewModel.saveTheme(theme)
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.ThemeSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 主畫面（暫時用文字顯示）
        composable(Screen.Main.route) {
            MainPlaceholder(settings)
        }
    }
}

@Composable
fun MainPlaceholder(settings: com.chi157.resignationpointscard.data.AppSettings?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "主畫面\n公司：${settings?.companyName}\n主題：${settings?.selectedTheme}",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}
