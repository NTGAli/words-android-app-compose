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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.Primary200
import com.ntg.mywords.ui.theme.fontMedium14
import com.ntg.mywords.vm.LoginViewModel
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel
){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.vocabs),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController = navController,
                wordViewModel = wordViewModel
            )

        }
    )
}


@Composable
private fun Content(paddingValues: PaddingValues,navController: NavController,wordViewModel: WordViewModel){

    LazyColumn(modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp)){

        item {
            Text(modifier = Modifier.padding(start = 8.dp), text = stringResource(id = R.string.your_lists), style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant))
        }

    }


}