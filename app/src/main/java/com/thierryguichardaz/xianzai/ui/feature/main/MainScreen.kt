package com.thierryguichardaz.xianzai.ui.feature.main

import androidx.compose.foundation.layout.Box // <-- ECCO L'IMPORT MANCANTE
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thierryguichardaz.xianzai.navigation.BottomNavItem // Importa la sealed class
import com.thierryguichardaz.xianzai.navigation.bottomNavItems // Importa la lista degli item
import com.thierryguichardaz.xianzai.ui.theme.XianZaiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // 1. Crea un NavController interno per la navigazione della Bottom Bar
    val innerNavController = rememberNavController()

    Scaffold(
        // Potremmo voler rendere dinamico il titolo in base alla schermata
        topBar = {
            // Determina il titolo corrente in base alla route
            val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val currentScreen = bottomNavItems.find { it.route == currentRoute }
            TopAppBar(
                title = { Text(currentScreen?.label ?: "XianZai") } // Usa label dell'item o default
            )
        },
        // 2. Definisci la Bottom Navigation Bar
        bottomBar = {
            BottomNavigationBar(navController = innerNavController)
        }
    ) { innerPadding ->
        // 3. Definisci l'host di navigazione per le schermate principali
        Box(modifier = Modifier.padding(innerPadding)) { // Applica il padding qui
            MainAppNavHost(navController = innerNavController)
        }
    }
}

// Composable per la Bottom Navigation Bar
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar { // Usa NavigationBar di Material 3
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { screen ->
            NavigationBarItem( // Usa NavigationBarItem di Material 3
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                // Optional: puoi controllare come vengono mostrate le label
                // alwaysShowLabel = true
            )
        }
    }
}

// Composable per il NavHost interno alla MainScreen
@Composable
fun MainAppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route // Inizia dalla Home
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen() // Mostra il contenuto della HomeScreen
        }
        composable(BottomNavItem.Calendar.route) {
            CalendarScreen() // Mostra il contenuto della CalendarScreen
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen(/* Qui potrai passare dati o callback in futuro */) // Mostra il contenuto della ProfileScreen
        }
        // Aggiungi qui altre destinazioni se necessario
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    XianZaiTheme {
        // La preview non avrà la navigazione completa funzionante,
        // ma mostrerà la struttura base della MainScreen
        MainScreen()
    }
}