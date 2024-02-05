package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.Table
import com.ntg.vocabs.model.data.GermanPronouns
import com.ntg.vocabs.model.data.VerbGermanForm
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium16
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerbsFormScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    word: String,
    type: String
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = type,
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                wordViewModel = wordViewModel, type, word
            )

        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    wordViewModel: WordViewModel,
    type: String,
    word: String
) {

    val verbData = wordViewModel.germanVerb(word).observeAsState().value

    when (type) {

        stringResource(id = R.string.indicative) -> {
            ShowVerbsFormTable(verbForm = verbData?.data?.indicative, paddingValues = paddingValues)
        }

        stringResource(id = R.string.conjunctive) -> {
            ShowVerbsFormTable(verbForm = verbData?.data?.conjunctive, paddingValues = paddingValues)
        }

        stringResource(id = R.string.conditional) -> {
            ShowVerbsFormTable(verbForm = verbData?.data?.conditional, paddingValues = paddingValues)
        }

        stringResource(id = R.string.imperaticve) -> {
            ImperativeForm(verbData?.data?.imperative, paddingValues)
        }

        else -> {
            verbData?.data?.indicative?.title = stringResource(id = R.string.indicative)
            verbData?.data?.conjunctive?.title = stringResource(id = R.string.conjunctive)
            verbData?.data?.conditional?.title = stringResource(id = R.string.conditional)
            ShowAll(verbForms = listOf(
                verbData?.data?.indicative,
                verbData?.data?.conditional,
                verbData?.data?.conjunctive,
            ), paddingValues = paddingValues, verbData?.data?.imperative)
        }

    }
}


@Composable
private fun ShowVerbsFormTable(verbForm: VerbGermanForm?, paddingValues: PaddingValues) {

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 24.dp),
        content = {

            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 16.dp),
                    text = stringResource(id = R.string.simple_present),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Table(
                    modifier = Modifier.padding(top = 16.dp),
                    pronouns = verbForm?.simplePresent
                )
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.present_perfect),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Table(
                    modifier = Modifier.padding(top = 16.dp),
                    pronouns = verbForm?.presentPerfect
                )
            }


            item {
                Text(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.simple_past),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Table(
                    modifier = Modifier.padding(top = 16.dp),
                    pronouns = verbForm?.simplePast
                )
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.past_perfect),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Table(
                    modifier = Modifier.padding(top = 16.dp),
                    pronouns = verbForm?.pastPerfect
                )
            }


            item {
                Text(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.future_one),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Table(
                    modifier = Modifier.padding(top = 16.dp),
                    pronouns = verbForm?.future_one
                )
            }

            item {
                Text(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.future_two),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Table(
                    modifier = Modifier.padding(top = 16.dp),
                    pronouns = verbForm?.future_two
                )
            }

            item {
                Spacer(modifier = Modifier.padding(32.dp))
            }

        })
}


@Composable
private fun ShowAll(
    verbForms: List<VerbGermanForm?>,
    paddingValues: PaddingValues,
    imperative: List<GermanPronouns>?
) {

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 24.dp),
        content = {

            items(verbForms){verbForm ->

                if (verbForm?.title != null){
                    Text(
                        modifier = Modifier.padding(top = 32.dp),
                        text = verbForm.title.orEmpty(),
                        style = fontMedium16(MaterialTheme.colorScheme.primary)
                    )
                }

                if (verbForm?.simplePresent.orEmpty().isNotEmpty()){
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(top = 16.dp),
                        text = stringResource(id = R.string.simple_present),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Table(
                        modifier = Modifier.padding(top = 16.dp),
                        pronouns = verbForm?.simplePresent
                    )
                }

                if (verbForm?.presentPerfect.orEmpty().isNotEmpty()){
                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.present_perfect),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Table(
                        modifier = Modifier.padding(top = 16.dp),
                        pronouns = verbForm?.presentPerfect
                    )
                }

                if (verbForm?.simplePast.orEmpty().isNotEmpty()){
                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.simple_past),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Table(
                        modifier = Modifier.padding(top = 16.dp),
                        pronouns = verbForm?.simplePast
                    )
                }


                if (verbForm?.pastPerfect.orEmpty().isNotEmpty()){
                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.past_perfect),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Table(
                        modifier = Modifier.padding(top = 16.dp),
                        pronouns = verbForm?.pastPerfect
                    )
                }


                if (verbForm?.future_one.orEmpty().isNotEmpty()){
                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.future_one),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Table(
                        modifier = Modifier.padding(top = 16.dp),
                        pronouns = verbForm?.future_one
                    )

                }


                if (verbForm?.future_two.orEmpty().isNotEmpty()){

                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.future_two),
                        style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Table(
                        modifier = Modifier.padding(top = 16.dp),
                        pronouns = verbForm?.future_two
                    )
                }

            }



            item {
                Text(
                    modifier = Modifier.padding(top = 32.dp),
                    text = stringResource(id = R.string.imperaticve),
                    style = fontMedium16(MaterialTheme.colorScheme.primary)
                )

                Table(
                    modifier = Modifier.padding(top = 16.dp),
                    pronouns = imperative
                )
            }


            item {
                Spacer(modifier = Modifier.padding(32.dp))
            }

        })
}

@Composable
private fun ImperativeForm(pronouns: List<GermanPronouns>?, paddingValues: PaddingValues){
    Column(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 24.dp)
    ) {

        Table(
            modifier = Modifier.padding(top = 16.dp),
            pronouns = pronouns
        )
    }
}