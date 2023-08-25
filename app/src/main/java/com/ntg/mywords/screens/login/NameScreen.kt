package com.ntg.mywords.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.components.EditText
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.ui.theme.fontMedium24

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameScreen(navController: NavController){

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(paddingValues = innerPadding, navController)
        }
    )

}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavController){

    var text by remember {
        mutableStateOf("I am no one \uD83E\uDD2D")
    }

    Column(modifier = Modifier.padding(horizontal = 32.dp)) {
        Text(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(vertical = 64.dp), text = text, style = fontMedium24(MaterialTheme.colorScheme.onBackground))


        EditText(label = stringResource(id = R.string.name), onChange = {
            if (it != ""){
                text = "I am $it \uD83D\uDE0E"
            }else{
                text = "I am no one \uD83E\uDD2D"
            }
        })

        CustomButton(text = if (text.contains("no one")) stringResource(id = R.string.prefer_not_to_say) else stringResource(
            id = R.string.next
        ), size = ButtonSize.LG)
    }

}