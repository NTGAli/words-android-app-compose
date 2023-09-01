package com.ntg.mywords.screens.setting

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.ItemOption
import com.ntg.mywords.nav.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController){

    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.settings),
                scrollBehavior = scrollBehavior,
                navigationOnClick = {
                    navController.popBackStack()
                }
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding,navController)

        }
    )


}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController){


    LazyColumn(modifier = Modifier.padding(paddingValues)){

        item {
            ItemOption(painter = painterResource(id = R.drawable.data_backup), text = stringResource(
                id = R.string.backup_and_restore
            )) {
                navController.navigate(Screens.BackupAndRestoreScreen.name)
            }
        }

    }


}