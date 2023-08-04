package com.ntg.mywords.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.CustomButton
import com.ntg.mywords.model.components.ButtonSize
import com.ntg.mywords.model.components.ButtonStyle
import com.ntg.mywords.model.components.ButtonType
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.*
import com.ntg.mywords.util.getStateRevision
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(navController: NavController, wordViewModel: WordViewModel) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.revision),
                scrollBehavior = scrollBehavior
            )
        },
        content = { innerPadding ->

            Content(paddingValues = innerPadding, wordViewModel, navController)

        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screens.AddEditScreen.name)
                },
                containerColor = Primary200
            ) {
                Icon(imageVector = Icons.Rounded.Add, tint = Color.Black, contentDescription = "FL")
            }
        }
    )

}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    navController: NavController,
) {

    var words = wordViewModel.getMyWords().observeAsState().value.orEmpty().filter {
        getStateRevision(
            it.revisionCount,
            it.lastRevisionTime
        ) == 2 || getStateRevision(
            it.revisionCount,
            it.lastRevisionTime
        ) == 3
    }

    val rejectedList = remember {
        mutableStateListOf<Word>()
    }

    words = words.filterNot { it in rejectedList }

    if (words.isNotEmpty()) {
        val word = words.get(0)
        LazyColumn(modifier = Modifier.padding(paddingValues)) {

            item {
                Text(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .padding(horizontal = 24.dp),
                    text = stringResource(id = R.string.do_you_remeber_this_word),
                    style = fontRegular14(
                        Secondary900
                    )
                )
            }

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Secondary100),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = word.word.orEmpty(), style = fontMedium24(Secondary900))
                        Text(modifier = Modifier.padding(start = 8.dp), text = word.type.orEmpty(), style = fontRegular14(Secondary500))
                    }


                    if (word.translation.orEmpty().isNotEmpty()){
                        Text(
                            modifier = Modifier.padding(top = 8.dp),
                            text = word.translation.orEmpty(),
                            style = fontMedium14(Secondary800)
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = word.pronunciation.orEmpty(),
                        style = fontMedium14(Secondary800)
                    )

                    Text(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(horizontal = 16.dp)
                            .align(Alignment.Start),
                        text = word.definition.orEmpty(),
                        style = fontRegular12(Secondary900)
                    )

                }

            }

            item {
                CustomButton(modifier = Modifier.padding(top = 16.dp).padding(horizontal = 24.dp), text = "yes", style = ButtonStyle.Contained, type = ButtonType.Success, size = ButtonSize.MD){
                    word.revisionCount = word.revisionCount + 1
                    word.lastRevisionTime = System.currentTimeMillis()
                    wordViewModel.editWord(word.id, word)
                    rejectedList.add(word)
                }

                CustomButton(modifier = Modifier.padding(top = 16.dp).padding(horizontal = 24.dp), text = "no", style = ButtonStyle.TextOnly, type = ButtonType.Danger, size = ButtonSize.MD){
                    rejectedList.add(word)
                }
            }

        }
    }else{
        rejectedList.clear()
    }


}