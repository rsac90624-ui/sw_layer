package com.example.sw_layer_application.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sw_layer_application.feature.map.MapScreen
import com.example.sw_layer_application.feature.projects.CreateProjectScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MAP
    ) {

        composable(Routes.MAP) {

            MapScreen(
                onCreateProjectClick = {
                    navController.navigate(Routes.CREATE_PROJECT)
                }
            )
        }

        composable(Routes.CREATE_PROJECT) {

            CreateProjectScreen(
                onProjectCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}