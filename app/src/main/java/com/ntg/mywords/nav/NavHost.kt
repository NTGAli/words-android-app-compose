package com.ntg.mywords.nav

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntg.mywords.screens.*
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.WordViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.HomeScreen.name,
    wordViewModel: WordViewModel,
    calendarViewModel: CalendarViewModel,
    onDestinationChangedListener:(NavController, NavDestination, Bundle?) -> Unit
) {

    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        onDestinationChangedListener(
            controller,
            destination,
            arguments
        )
    }


    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screens.HomeScreen.name) {
            HomeScreen(navController, wordViewModel)
        }

        composable(Screens.AllWordsScreen.name) {
            AllWordsScreen(navController, wordViewModel)
        }

        composable(Screens.RecentWordScreen.name) {
            RecentWordScreen(navController, wordViewModel)
        }

        composable(Screens.TimeScreen.name) {
            TimeScreen(navController, calendarViewModel)
        }


        composable(Screens.RevisionScreen.name) {
            RevisionScreen(navController, wordViewModel, calendarViewModel)
        }

        composable(
            Screens.AddEditScreen.name + "?wordId={wordId}",
            arguments = listOf(navArgument("wordId")
            {
                type = NavType.IntType
                defaultValue = -1
            })
        )
        { backStackEntry ->
            AddEditWordScreen(
                navController,
                wordViewModel,
                backStackEntry.arguments?.getInt("wordId")
            )
        }

        composable(
            Screens.WordDetailScreen.name + "?wordId={wordId}",
            arguments = listOf(navArgument("wordId")
            {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            WordDetailScreen(
                navController = navController,
                wordViewModel = wordViewModel,
                backStackEntry.arguments?.getInt("wordId")
            )
        }
    }

}