package com.ntg.mywords.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.ntg.mywords.R
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.SampleItem
import com.ntg.mywords.components.ShapeTileWidget
import com.ntg.mywords.model.db.Word
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.FontBold14
import com.ntg.mywords.ui.theme.Primary200
import com.ntg.mywords.ui.theme.Secondary900
import com.ntg.mywords.util.getDaysBetweenTimestamps
import com.ntg.mywords.util.getStateRevision
import com.ntg.mywords.util.orDefault
import com.ntg.mywords.util.timber
import com.ntg.mywords.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, wordViewModel: WordViewModel) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.my_words),
                enableNavigation = false,
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
private fun Content(paddingValues: PaddingValues, wordViewModel: WordViewModel, navController: NavController) {

    val wordsList: State<List<Word>?> = wordViewModel.getMyWords().observeAsState()

    LazyColumn(modifier = Modifier.padding(paddingValues)) {

        item {

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp),
                text = stringResource(R.string.workout_report),
                style = FontBold14(
                    Secondary900
                )
            )

            Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    title = "12 words",
                    subTitle = "last 7d ago",
                    painter = painterResource(
                        id = R.drawable.ic_new
                    )
                ) {

                }

                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp), title = "title", subTitle = "14", painter = painterResource(
                        id = R.drawable.ic_new
                    )
                )
            }

            Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    title = "12 words",
                    subTitle = "last 7d ago",
                    painter = painterResource(
                        id = R.drawable.ic_new
                    )
                ) {

                }

                ShapeTileWidget(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp), title = "title", subTitle = "14", painter = painterResource(
                        id = R.drawable.ic_new
                    )
                )
            }

            Text(
                modifier = Modifier.padding(top = 28.dp, start = 16.dp),
                text = stringResource(R.string.words),
                style = FontBold14(Secondary900)
            )

        }


        items(wordsList.value.orEmpty()){word ->

            val painter = getStateRevision(word.revisionCount, word.lastRevisionTime)

            SampleItem(modifier = Modifier.padding(horizontal = 16.dp),title = word.word.toString(), id = word.id, painter = painter){ title , id ->

                timber("kawljdlkajwdlkjawlkdj $id")

                navController.navigate(Screens.AddEditScreen.name+"?wordId=$id")

            }
        }


    }


}


