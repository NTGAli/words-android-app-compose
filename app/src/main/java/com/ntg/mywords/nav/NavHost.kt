package com.ntg.mywords.nav

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntg.mywords.screens.*
import com.ntg.mywords.screens.login.*
import com.ntg.mywords.screens.setting.BackupAndRestoreScreen
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.HomeScreen.name,
    wordViewModel: WordViewModel,
    calendarViewModel: CalendarViewModel,
    loginViewModel: LoginViewModel,
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

        composable(Screens.SelectLanguageScreen.name) {
            SelectLanguageScreen(navController)
        }

        composable(Screens.TimeScreen.name) {
            TimeScreen(navController, calendarViewModel)
        }

        composable(Screens.SettingScreen.name) {
            SettingScreen(navController)
        }

        composable(Screens.CodeScreen.name) {
            CodeScreen(navController, "", loginViewModel)
        }

        composable(Screens.CodeScreen.name+ "?email={email}",
            arguments = listOf(navArgument("email")
            {
                type = NavType.StringType
                defaultValue = "your"
            })
        ) {
            CodeScreen(navController, it.arguments?.getString("email").orEmpty(), loginViewModel)
        }

        composable(Screens.LoginWithPasswordScreen.name+ "?email={email}",
            arguments = listOf(navArgument("email")
            {
                type = NavType.StringType
                defaultValue = "your"
            })
        ) {
            LoginWithPasswordScreen(navController, it.arguments?.getString("email").orEmpty(), loginViewModel)
        }

        composable(Screens.BackupAndRestoreScreen.name) {
            BackupAndRestoreScreen(navController, wordViewModel)
        }

        composable(Screens.InsertEmailScreen.name) {
            InsertEmailScreen(navController, loginViewModel)
        }

        composable(Screens.VocabularyListScreen.name) {
            VocabularyListScreen(navController)
        }

        composable(Screens.NameScreen.name+ "?email={email}",
            arguments = listOf(navArgument("email")
            {
                type = NavType.StringType
                defaultValue = "-"
            })
        ) {
            NameScreen(navController, loginViewModel, it.arguments?.getString("email").orEmpty())
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