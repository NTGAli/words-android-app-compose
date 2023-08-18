package com.ntg.mywords.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.ItemOption
import com.ntg.mywords.db.AppDatabaseBackup
import com.ntg.mywords.model.components.AppbarItem
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.Primary200
import com.ntg.mywords.util.Constant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController){

    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.setting),
                scrollBehavior = scrollBehavior
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