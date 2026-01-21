package com.chi157.resignationpointscard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chi157.resignationpointscard.ui.theme.DarkBlueBackground

sealed class Screen(val route: String) {
    object CompanyName : Screen("company_name")
    object StampCountSelection : Screen("stamp_count_selection")
    object ThemeSelection : Screen("theme_selection")
    object Main : Screen("main")
    object Plan : Screen("plan")
    object Record : Screen("record")
    object Settings : Screen("settings")
    object Success : Screen("success")
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
    
    // 決定起始畫面 (新順序：公司名稱 -> 選擇風格 -> 選擇格數)
    val startDestination = when {
        settings?.isOnboardingCompleted == true -> Screen.Main.route
        settings?.companyName?.isEmpty() == true -> Screen.CompanyName.route
        settings?.selectedTheme?.isEmpty() == true -> Screen.ThemeSelection.route
        (settings?.targetStamps ?: 0) == 0 -> Screen.StampCountSelection.route
        else -> Screen.Main.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. 公司名稱畫面
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
        
        // 2. 主題選擇畫面
        composable(Screen.ThemeSelection.route) {
            ThemeSelectionScreen(
                onThemeSelected = { theme ->
                    viewModel.saveTheme(theme)
                    navController.navigate(Screen.StampCountSelection.route) {
                        popUpTo(Screen.ThemeSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 3. 格數選擇畫面
        composable(Screen.StampCountSelection.route) {
            StampCountSelectionScreen(
                onStampCountSelected = { count ->
                    viewModel.saveTargetStamps(count)
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.StampCountSelection.route) { inclusive = true }
                    }
                }
            )
        }
        
        // 4. 正式主畫面 (集點卡)
        composable(Screen.Main.route) {
            MainPointsCardScreen(
                viewModel = viewModel,
                onNavigateToPlan = { navController.navigate(Screen.Plan.route) },
                onNavigateToRecord = { navController.navigate(Screen.Record.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToSuccess = { navController.navigate(Screen.Success.route) }
            )
        }
        
        // 5. 離職計畫
        composable(Screen.Plan.route) {
            ResignationPlanScreen(viewModel = viewModel, navController = navController)
        }
        
        // 6. 離職紀錄
        composable(Screen.Record.route) {
            ResignationRecordScreen(viewModel = viewModel, navController = navController)
        }
        
        // 7. 設定畫面
        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                viewModel = viewModel,
                onReset = { 
                    viewModel.resetAllData()
                    navController.navigate(Screen.CompanyName.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navController = navController
            )
        }
        
        // 8. 離職成功畫面
        composable(Screen.Success.route) {
            ResignationSuccessScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRecord = {
                    navController.navigate(Screen.Record.route)
                }
            )
        }
    }
}



@Composable
fun TabPlaceholder(route: String, title: String, viewModel: AppViewModel, navController: NavHostController) {
    Scaffold(
        bottomBar = {
            MainBottomNavigation(currentRoute = route, onNavigate = { targetRoute ->
                navController.navigate(targetRoute)
            })
        },
        containerColor = DarkBlueBackground
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(text = title, color = Color.White, fontSize = 24.sp)
        }
    }
}
