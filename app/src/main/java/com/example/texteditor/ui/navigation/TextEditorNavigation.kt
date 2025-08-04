package com.example.texteditor.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.texteditor.ui.components.SettingsScreen
import com.example.texteditor.ui.components.TextEditorScreen

sealed class Screen(val route: String) {
    object Startup : Screen("startup")
    object Editor : Screen("editor")
    object Settings : Screen("settings")
}

@Composable
fun TextEditorNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Editor.route
    ) {
        composable(Screen.Editor.route) {
            TextEditorScreen(
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
} 