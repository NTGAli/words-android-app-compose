package com.ntg.mywords.screens.login

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.EditText
import com.ntg.mywords.components.ItemSelectable
import com.ntg.mywords.components.TypewriterText
import com.ntg.mywords.ui.theme.fontMedium14

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLanguageScreen(navController: NavController){

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
    val list = listOf<String>(
        "english",
        "franch",
        "adawdwa",
        "wdwadwad"
    )

    var language by remember {
        mutableStateOf("english")
    }


    LazyColumn(modifier = Modifier.padding(horizontal = 32.dp)){

        item {
            TypewriterText(modifier = Modifier.padding(top = 64.dp, bottom = 32.dp), texts = listOf(stringResource(id = R.string.witch_language)), singleText = true, speedType = 20L)
        }

        items(list){

            ItemSelectable(modifier =Modifier.padding(top = 8.dp), text = it, isSelected = language==it){
                language = it
            }

        }

        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(width = 2.dp, shape = RoundedCornerShape(16.dp), color = if (language in list) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary)
                ){

                Text(modifier = Modifier.padding(vertical = 16.dp).padding(start = 24.dp), text = "other", style = fontMedium14(
                    MaterialTheme.colorScheme.onBackground)
                )

                EditText(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), label = "name"){
                    language = it
                }


            }
        }

    }
}