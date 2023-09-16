package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.UserDataAndSetting
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.CheckboxText
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.ui.theme.fontRegular12
import com.ntg.mywords.util.timber
import com.ntg.mywords.util.toast
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(navController: NavController, loginViewModel: LoginViewModel, wordViewModel: WordViewModel) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            val userData = loginViewModel.getUserData().asLiveData().observeAsState()
            Content(paddingValues = innerPadding, navController, loginViewModel, userData.value, wordViewModel)
        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    loginViewModel: LoginViewModel,
    userData: UserDataAndSetting?,
    wordViewModel: WordViewModel
) {

    val owner = LocalLifecycleOwner.current
    val ctx = LocalContext.current


    val password = remember {
        mutableStateOf("")
    }


    var loading by remember {
        mutableStateOf(false)
    }

    val optionOneChecked = remember {
        mutableStateOf(false)
    }
    val optionTwoChecked = remember {
        mutableStateOf(false)
    }
    val openBottomSheet = remember {
        mutableStateOf(false)
    }


    if (openBottomSheet.value) {
        logoutBottomSheet(openBottomSheet) {
            wordViewModel.clearWordsTable()
            wordViewModel.clearVocabListsTable()
            wordViewModel.clearTimesTable()
            loginViewModel.clearUserData()
            openBottomSheet.value = false
            navController.navigate(Screens.InsertEmailScreen.name) {
                popUpTo(0)
            }
        }

    }


    if (loading) {

        loginViewModel.deleteAccount(
            password = password.value,
            email = userData?.email.orEmpty()
        ).observe(owner) {

            when (it) {
                is NetworkResult.Error -> {
                    ctx.toast(ctx.getString(R.string.sth_wrong))
                }
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Success -> {
                    loading = false

                    when (it.data?.message) {

                        "INVALID_TOKEN" -> {
                            ctx.toast(ctx.getString(R.string.download_from_google_play))
                        }

                        "password is incorrect" -> {
                            ctx.toast(ctx.getString(R.string.password_not_match))
                        }

                        "user removed" -> {
                            loginViewModel.clearUserData()
                            navController.navigate(Screens.FinishScreen.name) {
                                popUpTo(0)
                            }
                        }
                    }


                }

            }
        }


    }

    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        TypewriterText(
            modifier = Modifier
                .padding(vertical = 64.dp)
                .align(Alignment.CenterHorizontally),
            texts = listOf(stringResource(id = R.string.delete_account)),
            singleText = true
        )


        EditText(label = stringResource(id = R.string.password), isPassword = true, text = password)

        Text(
            modifier = Modifier.padding(vertical = 24.dp),
            text = stringResource(id = R.string.title_delete_account),
            style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        CheckboxText(
            text = stringResource(id = R.string.option_one_delete_account),
            enabled = !loading,
            checked = optionOneChecked
        )
        CheckboxText(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.option_two_delete_account),
            enabled = !loading,
            checked = optionTwoChecked
        )

        Text(
            modifier = Modifier.padding(top = 24.dp, bottom = 32.dp),
            text = stringResource(id = R.string.footer_delete_account),
            style = fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
        )

        CustomButton(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(
                id = R.string.delete_my_account
            ),
            size = ButtonSize.LG,
            loading = loading,
            type = ButtonType.Danger,
            enable = optionOneChecked.value && optionTwoChecked.value && password.value.isNotEmpty()
        ) {
            loading = true
        }


        CustomButton(
            modifier = Modifier
                .fillMaxWidth(), text = stringResource(
                id = R.string.logout_instead
            ), size = ButtonSize.LG, type = ButtonType.Danger, style = ButtonStyle.TextOnly
        ) {
            openBottomSheet.value = true

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun logoutBottomSheet(
    openBottomSheet: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { openBottomSheet.value = false }
    ) {

        Column(
            Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.before_logout), style = fontMedium14(
                    MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.logout_message), style = fontRegular12(
                    MaterialTheme.colorScheme.onBackground
                )
            )

            Row(modifier = Modifier.padding(top = 16.dp)) {
                CustomButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    text = stringResource(id = R.string.cancel),
                    type = ButtonType.Secondary,
                    style = ButtonStyle.Outline
                ) {
                    openBottomSheet.value = false
                }
                CustomButton(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                    text = stringResource(id = R.string.sign_out),
                    type = ButtonType.Danger
                ) {
                    onClick.invoke()
                }
            }
        }


    }
}