package com.ntg.mywords.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntg.mywords.screens.AddEditWordScreen
import com.ntg.mywords.screens.HomeScreen
import com.ntg.mywords.vm.WordViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.HomeScreen.name,
    wordViewModel: WordViewModel
) {


    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screens.HomeScreen.name) {
            HomeScreen(navController)
        }

        composable(Screens.AddEditScreen.name) {
            AddEditWordScreen(navController, wordViewModel)
        }
    }

}