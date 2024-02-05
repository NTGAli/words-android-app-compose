package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.checkInternet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoInternetConnection(
    navController: NavController,
    screen: String?
){
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.can_not_load),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack(Screens.HomeScreen.name, inclusive = false) }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, screen, navController)

        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    screen: String?,
    navController: NavController
){

    val context = LocalContext.current

    Column(modifier = Modifier.padding(paddingValues).fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(painter = painterResource(id = R.drawable.wifi_off), contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Text(modifier = Modifier.padding(top = 8.dp),text = stringResource(id = R.string.no_internet), style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant))
        CustomButton(modifier = Modifier.padding(top = 8.dp),text = stringResource(id = R.string.try_again), style = ButtonStyle.TextOnly){
            if (context.checkInternet()){
                navController.popBackStack()
                navController.navigate(screen ?: Screens.HomeScreen.name)
            }
        }
    }


}