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
import com.ntg.vocabs.screens.intro.ExplainSubscriptionScreen
import com.ntg.vocabs.screens.intro.IntroScreen
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
            HomeScreen(navController, wordViewModel, loginViewModel, backupViewModel, messageBoxViewModel)
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
                loginViewModel,
                it.arguments?.getBoolean("openSearch").orFalse(),
                it.arguments?.getString("query").orEmpty()
            )
        }

        composable(Screens.RecentWordScreen.name) {
            RecentWordScreen(navController, wordViewModel, loginViewModel)
        }

        composable(
            Screens.SelectLanguageScreen.name + "?listId={listId}",
            arguments = listOf(navArgument("listId")
            {
                type = NavType.IntType
                defaultValue = -1
            })
        ) {
            SelectLanguageScreen(navController, wordViewModel,loginViewModel, it.arguments?.getInt("listId"))
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

        composable(route = Screens.TermsAndConditionsScreen.name) {
            TermsAndConditionsScreen(navController)
        }

        composable(Screens.ThemeScreen.name) {
            ThemeScreen(navController, loginViewModel)
        }

        composable(Screens.FullScreenAdScreen.name) {
            FullScreenAdScreen(navController, messageBoxViewModel)
        }

        composable(Screens.SubscriptionsScreen.name) {
            SubscriptionsScreen(navController, loginViewModel)
        }

        composable(Screens.SelectReviewTypeScreen.name) {
            SelectReviewTypeScreen(navController,wordViewModel, loginViewModel)
        }

        composable(Screens.WritingScreen.name) {
            WritingScreen(navController,wordViewModel)
        }

        composable(Screens.SelectBackupOptionsScreen.name) {
            SelectBackupOptionsScreen(navController,backupViewModel, loginViewModel)
        }

        composable(Screens.NoBackupScreen.name) {
            NoBackupScreen(navController,backupViewModel)
        }

        composable(Screens.PaywallScreen.name) {
            PaywallScreen(navController,loginViewModel)
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
                backupViewModel,
                it.arguments?.getBoolean("skip").orTrue()
            )
        }

        composable(
            Screens.GoogleLoginScreen.name + "?skip={skip}",
            arguments = listOf(navArgument("skip") {
                type = NavType.BoolType
                defaultValue = true
            })
        ) {
            GoogleLoginScreen(
                navController,
                loginViewModel,
                signInViewModel,
                backupViewModel,
                it.arguments?.getBoolean("skip").orTrue()
            )
        }

        composable(Screens.VocabularyListScreen.name) {
            VocabularyListScreen(navController, wordViewModel, loginViewModel, backupViewModel)
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


        composable(Screens.RevisionScreen.name + "?isRandom={isRandom}",
            arguments = listOf(navArgument("isRandom")
            {
                type = NavType.BoolType
                defaultValue = false
            })) {
            RevisionScreen(navController, wordViewModel, calendarViewModel,it.arguments?.getBoolean("isRandom").orFalse())
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
            SearchScreen(navController, wordViewModel, loginViewModel)
        }

        composable(Screens.IntroScreen.name) {
            IntroScreen(navController, loginViewModel)
        }

        composable(Screens.ExplainSubscriptionScreen.name) {
            ExplainSubscriptionScreen(navController, loginViewModel)
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
            BackupScreen(navController,backupViewModel,loginViewModel)
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
                loginViewModel,
                backStackEntry.arguments?.getInt("wordId")
            )
        }

        composable(
            Screens.RestoringBackupOnServerScreen.name + "?email={email}",
            arguments = listOf(navArgument("email")
            {
                type = NavType.StringType
                defaultValue = ""
            })
        )
        { backStackEntry ->
            RestoringBackupOnServerScreen(
                navController,
                backupViewModel,
                loginViewModel,
                backStackEntry.arguments?.getString("email").orEmpty()
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
            Screens.WordDetailScreen.name + "?wordId={wordId}&index={index}",
            arguments = listOf(
                navArgument("wordId")
            {
                type = NavType.IntType
                defaultValue = -1
            },
                navArgument("index")
                {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            WordDetailScreen(
                navController = navController,
                wordViewModel = wordViewModel,
                loginViewModel,
                backStackEntry.arguments?.getInt("wordId"),
                backStackEntry.arguments?.getInt("index"),
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
                loginViewModel,
                backStackEntry.arguments?.getString("word").orEmpty(),
                backStackEntry.arguments?.getString("type").orEmpty()
            )
        }

        composable(
            Screens.SuccessPurchaseScreen.name + "?type={type}",
            arguments = listOf(
                navArgument("type")
                {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) { backStackEntry ->
            SuccessPurchaseScreen(
                navController = navController,
                loginViewModel,
                backStackEntry.arguments?.getString("type").orEmpty()
            )
        }

        composable(
            Screens.ReviewAiScreen.name
        ) {
            ReviewAiScreen(
                navController = navController,
                wordViewModel = wordViewModel
            )
        }
    }

}


