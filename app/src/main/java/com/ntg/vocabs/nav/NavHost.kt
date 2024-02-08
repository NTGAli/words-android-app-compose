package com.ntg.vocabs.nav

import android.os.Bundle
import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntg.vocabs.components.FullScreenImageScreen
import com.ntg.vocabs.screens.*
import com.ntg.vocabs.screens.login.*
import com.ntg.vocabs.screens.setting.SettingScreen
import com.ntg.vocabs.screens.setting.ThemeScreen
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orTrue
import com.ntg.vocabs.vm.BackupViewModel
import com.ntg.vocabs.vm.CalendarViewModel
import com.ntg.vocabs.vm.DataViewModel
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.MessageBoxViewModel
import com.ntg.vocabs.vm.SignInViewModel
import com.ntg.vocabs.vm.WordViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier.fillMaxSize(),
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screens.SplashScreen.name,
    wordViewModel: WordViewModel,
    calendarViewModel: CalendarViewModel,
    loginViewModel: LoginViewModel,
    signInViewModel: SignInViewModel,
    messageBoxViewModel: MessageBoxViewModel,
    dataViewModel: DataViewModel,
    backupViewModel: BackupViewModel,
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
//        enterTransition = { EnterTransition.None },
//        exitTransition = { ExitTransition.None }
    ) {

        composable(Screens.SplashScreen.name, enterTransition = { ->
            EnterTransition.None
        }) {
            SplashScreen(navController, loginViewModel, wordViewModel)
        }

        composable(Screens.HomeScreen.name, enterTransition = { ->
            EnterTransition.None
        }) {
            HomeScreen(navController, wordViewModel, loginViewModel, backupViewModel)
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


        composable(
            Screens.NoInternetConnection.name + "?screen={screen}",
            arguments = listOf(navArgument("screen")
            {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            })
        ) {
            NoInternetConnection(navController, it.arguments?.getString("screen").orEmpty())
        }


        composable(Screens.RevisionScreen.name) {
            RevisionScreen(navController, wordViewModel, calendarViewModel)
        }

        composable(Screens.ProfileScreen.name) {
            ProfileScreen(navController, wordViewModel, loginViewModel, calendarViewModel)
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

        composable(Screens.SearchScreen.name) {
            SearchScreen(navController, wordViewModel)
        }

        composable(
            Screens.DownloadScreen.name + "?enableBottomBar={enableBottomBar}",
            arguments = listOf(navArgument("enableBottomBar")
            {
                type = NavType.BoolType
                defaultValue = false
            })
        ) {
            DownloadScreen(
                navController,
                dataViewModel,
                wordViewModel,
                it.arguments?.getBoolean("enableBottomBar").orFalse()
            )
        }

        composable(Screens.FinishScreen.name) {
            FinishScreen(navController)
        }

        composable(Screens.HelpAndFeedbackScreen.name) {
            HelpAndFeedbackScreen(navController)
        }

        composable(Screens.MessagesBoxScreen.name) {
            MessagesBoxScreen(navController, messageBoxViewModel)
        }

        composable(Screens.AskBackupScreen.name) {
            AskBackupScreen(navController, backupViewModel,loginViewModel)
        }

        composable(Screens.BackupScreen.name) {
            BackupScreen(navController,backupViewModel)
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
            Screens.VerbsFormScreen.name + "?verb={verb}&form={form}",
            arguments = listOf(
                navArgument("verb")
                {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("form")
                {
                    type = NavType.StringType
                    defaultValue = "indicative"
                })
        )
        { backStackEntry ->
            VerbsFormScreen(
                navController,
                wordViewModel,
                backStackEntry.arguments?.getString("verb").orEmpty(),
                backStackEntry.arguments?.getString("form").orEmpty()
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

        composable(
            Screens.FullScreenImageScreen.name + "?path={path}",
            arguments = listOf(navArgument("path")
            {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->
            FullScreenImageScreen(
                navController = navController,
                backStackEntry.arguments?.getString("path")
            )
        }


        composable(
            Screens.OnlineWordDetailsScreen.name + "?word={word}&type={type}",
            arguments = listOf(navArgument("word")
            {
                type = NavType.StringType
                defaultValue = ""
            },
                navArgument("type")
                {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) { backStackEntry ->
            OnlineWordDetailsScreen(
                navController = navController,
                wordViewModel = wordViewModel,
                backStackEntry.arguments?.getString("word").orEmpty(),
                backStackEntry.arguments?.getString("type").orEmpty()
            )
        }
    }

}


