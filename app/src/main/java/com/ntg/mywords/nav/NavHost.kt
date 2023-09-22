package com.ntg.mywords.nav

import android.os.Bundle
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntg.mywords.screens.*
import com.ntg.mywords.screens.login.*
import com.ntg.mywords.screens.setting.SettingScreen
import com.ntg.mywords.screens.setting.ThemeScreen
import com.ntg.mywords.util.orFalse
import com.ntg.mywords.util.orTrue
import com.ntg.mywords.vm.CalendarViewModel
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.SignInViewModel
import com.ntg.mywords.vm.WordViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.SplashScreen.name,
    wordViewModel: WordViewModel,
    calendarViewModel: CalendarViewModel,
    loginViewModel: LoginViewModel,
    signInViewModel: SignInViewModel,
    onDestinationChangedListener: (NavController, NavDestination, Bundle?) -> Unit
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
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {

        composable(Screens.SplashScreen.name, enterTransition = {  ->
            EnterTransition.None
        }) {
            SplashScreen(navController, loginViewModel, wordViewModel)
        }

        composable(Screens.HomeScreen.name,enterTransition = {  ->
            EnterTransition.None
        }) {
            HomeScreen(navController, wordViewModel, loginViewModel)
        }

        composable(Screens.AllWordsScreen.name + "?openSearch={openSearch}" + "&query={query}",
            arguments = listOf(navArgument("openSearch")
            {
                type = NavType.BoolType
                defaultValue = false
            },
                navArgument("query")
                {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )) {
            AllWordsScreen(
                navController,
                wordViewModel,
                it.arguments?.getBoolean("openSearch").orFalse(),
                it.arguments?.getString("query").orEmpty()
            )
        }

        composable(Screens.RecentWordScreen.name) {
            RecentWordScreen(navController, wordViewModel)
        }

        composable(
            Screens.SelectLanguageScreen.name + "?listId={listId}",
            arguments = listOf(navArgument("listId")
            {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            SelectLanguageScreen(navController, wordViewModel, it.arguments?.getInt("listId"))
        }

        composable(Screens.TimeScreen.name) {
            TimeScreen(navController, calendarViewModel, wordViewModel)
        }

        composable(Screens.SettingScreen.name) {
            SettingScreen(navController, loginViewModel, wordViewModel)
        }

        composable(route = Screens.CodeScreen.name) {
            CodeScreen(navController, "", loginViewModel)
        }

        composable(route = Screens.PrivacyPolicyScreen.name) {
            PrivacyPolicyScreen(navController)
        }

        composable(Screens.ThemeScreen.name) {
            ThemeScreen(navController, loginViewModel)
        }

        composable(
            Screens.CodeScreen.name + "?email={email}",
            arguments = listOf(navArgument("email")
            {
                type = NavType.StringType
                defaultValue = "your"
            })
        ) {
            CodeScreen(navController, it.arguments?.getString("email").orEmpty(), loginViewModel)
        }

        composable(
            Screens.LoginWithPasswordScreen.name + "?email={email}&isNew={isNew}",
            arguments = listOf(navArgument("email")
            {
                type = NavType.StringType
                defaultValue = "your"
            },
                navArgument("isNew")
                {
                    type = NavType.BoolType
                    defaultValue = false
                })
        ) {
            LoginWithPasswordScreen(
                navController,
                it.arguments?.getString("email").orEmpty(),
                it.arguments?.getBoolean("isNew").orFalse(),
                loginViewModel
            )
        }

//        composable(Screens.BackupAndRestoreScreen.name) {
//            BackupAndRestoreScreen(navController, wordViewModel)
//        }

        composable(
            Screens.InsertEmailScreen.name + "?skip={skip}",
            arguments = listOf(navArgument("skip") {
                type = NavType.BoolType
                defaultValue = true
            })
        ) {
            InsertEmailScreen(
                navController,
                loginViewModel,
                signInViewModel,
                it.arguments?.getBoolean("skip").orTrue()
            )
        }

        composable(Screens.VocabularyListScreen.name) {
            VocabularyListScreen(navController, wordViewModel, loginViewModel)
        }

        composable(
            Screens.NameScreen.name + "?email={email}",
            arguments = listOf(navArgument("email")
            {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {
            NameScreen(navController, loginViewModel, it.arguments?.getString("email").orEmpty())
        }


        composable(Screens.RevisionScreen.name) {
            RevisionScreen(navController, wordViewModel, calendarViewModel)
        }

        composable(Screens.ProfileScreen.name) {
            ProfileScreen(navController, wordViewModel, loginViewModel)
        }

        composable(Screens.BookmarkScreen.name) {
            BookmarkScreen(navController, wordViewModel, false, "")
        }

        composable(Screens.UpdateEmailScreen.name) {
            UpdateEmailScreen(navController, loginViewModel)
        }

        composable(Screens.DeleteAccountScreen.name) {
            DeleteAccountScreen(navController, loginViewModel, wordViewModel)
        }

        composable(Screens.FinishScreen.name) {
            FinishScreen(navController)
        }

        composable(Screens.HelpAndFeedbackScreen.name) {
            HelpAndFeedbackScreen(navController)
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


