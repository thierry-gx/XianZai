package com.thierryguichardaz.xianzai // Package corretto per MainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.DatePicker // Verifica questo
import androidx.compose.material3.DatePickerDialog // Verifica questo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker // Verifica questo
import androidx.compose.material3.rememberDatePickerState // Verifica questo
import androidx.compose.material3.rememberTimePickerState // Verifica questo
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.thierryguichardaz.xianzai.data.preferences.UserPreferencesRepository
import com.thierryguichardaz.xianzai.navigation.AppRoutes
// Importa MainScreen dal suo file corretto (MainScreen.kt)!
import com.thierryguichardaz.xianzai.ui.feature.main.MainScreen
import com.thierryguichardaz.xianzai.ui.feature.onboarding.QuizScreen
import com.thierryguichardaz.xianzai.ui.feature.onboarding.UserInfoScreen
import com.thierryguichardaz.xianzai.ui.theme.XianZaiTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {

    private lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userPreferencesRepository = UserPreferencesRepository(applicationContext)

        setContent {
            XianZaiTheme {
                var isLoadingPreferences by remember { mutableStateOf(true) }
                var startRoute by remember { mutableStateOf<String?>(null) }

                // Legge le preferenze una sola volta all'avvio
                LaunchedEffect(key1 = Unit) {
                    val isOnboardingComplete = userPreferencesRepository.isOnboardingComplete.first()
                    startRoute = if (isOnboardingComplete) {
                        AppRoutes.MAIN_APP
                    } else {
                        AppRoutes.ONBOARDING_QUIZ
                    }
                    isLoadingPreferences = false
                }

                // Mostra caricamento finché non abbiamo la route iniziale
                if (isLoadingPreferences) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Mostra la navigazione una volta determinata la route iniziale
                    startRoute?.let { route ->
                        AppNavigation(
                            startDestination = route,
                            userPreferencesRepository = userPreferencesRepository
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    userPreferencesRepository: UserPreferencesRepository
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppRoutes.ONBOARDING_QUIZ) {
            QuizScreen(
                onQuizComplete = { determinedChronotype ->
                    // Naviga a User Info, passando il cronotipo
                    navController.navigate(
                        AppRoutes.ONBOARDING_USER_INFO.replace("{chronotype}", determinedChronotype)
                    ) {
                        popUpTo(AppRoutes.ONBOARDING_QUIZ) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = AppRoutes.ONBOARDING_USER_INFO,
            arguments = listOf(navArgument("chronotype") { type = NavType.StringType })
        ) { backStackEntry ->
            val chronotype = backStackEntry.arguments?.getString("chronotype") ?: "Intermediate"
            UserInfoScreen(
                chronotype = chronotype,
                userPreferencesRepository = userPreferencesRepository, // Passa il repo a UserInfoScreen
                onUserInfoComplete = {
                    // La logica per salvare è ora dentro UserInfoScreen
                    // Naviga alla schermata principale
                    navController.navigate(AppRoutes.MAIN_APP) {
                        // Pulisce lo stack di onboarding
                        popUpTo(AppRoutes.ONBOARDING_USER_INFO.replace("{chronotype}", chronotype)) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppRoutes.MAIN_APP) {
            // QUESTA è la chiamata alla funzione MainScreen()
            // che è definita CORRETTAMENTE nel file MainScreen.kt
            MainScreen()
        }
    }
}