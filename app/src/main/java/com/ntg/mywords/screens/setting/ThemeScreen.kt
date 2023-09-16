package com.ntg.mywords.screens.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.ItemOption
import com.ntg.mywords.util.UserStore
import com.ntg.mywords.vm.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(navController: NavController, loginViewModel: LoginViewModel) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.theme),
                scrollBehavior = scrollBehavior,
                navigationOnClick = {
                    navController.popBackStack()
                }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, navController, loginViewModel)

        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val ctx = LocalContext.current



    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        item {

            ItemOption(text = stringResource(id = R.string.light_mode), divider = true) {
                loginViewModel.setTheme(ctx.getString(R.string.light_mode))
            }

            ItemOption(text = stringResource(id = R.string.dark_mode), divider = true) {
                loginViewModel.setTheme(ctx.getString(R.string.dark_mode))
            }

            ItemOption(text = stringResource(id = R.string.system_default), divider = true) {
                loginViewModel.setTheme(ctx.getString(R.string.system_default))
            }


        }

    }

}