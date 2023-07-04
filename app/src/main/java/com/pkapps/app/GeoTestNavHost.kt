package com.pkapps.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pkapps.app.screen.countrypicker.CountryPickerScreen
import com.pkapps.app.screen.quiz.QuizScreen
import com.pkapps.app.screen.welcome.WelcomeScreen

@Composable
fun GeoTestNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "Welcome"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("Welcome") {
            WelcomeScreen(
                onChangeCountry = { navController.navigate("CountryPicker") },
                onContinue = { navController.navigate("Quiz") }
            )
        }
        composable("CountryPicker") {
            CountryPickerScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNewPick = {
                    navController.navigate("Welcome") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable("Quiz") {
            QuizScreen(
                onStartAgain = { navController.popBackStack() }
            )
        }
    }
}