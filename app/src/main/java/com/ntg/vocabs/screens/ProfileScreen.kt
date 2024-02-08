package com.ntg.vocabs.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.*
import com.ntg.vocabs.model.SpendTimeType
import com.ntg.vocabs.model.VocabsListWithCount
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontRegular12
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.orZero
import com.ntg.vocabs.vm.CalendarViewModel
import com.ntg.vocabs.vm.LoginViewModel
import com.ntg.vocabs.vm.WordViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    calendarViewModel: CalendarViewModel
) {

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
                wordViewModel = wordViewModel,
                loginViewModel = loginViewModel,
                calendarViewModel = calendarViewModel
            )

        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    wordViewModel: WordViewModel,
    loginViewModel: LoginViewModel,
    calendarViewModel: CalendarViewModel
) {

    val list = remember {
        mutableStateOf(listOf<VocabsListWithCount>())
    }
    val listId = remember {
        mutableStateOf(-1)
    }

    var deleteList by remember {
        mutableStateOf(false)
    }

    var visible by remember {
        mutableStateOf(false)
    }

    var openBottomSheet by remember {
        mutableStateOf(false)
    }
    list.value = wordViewModel.getListWithCount()
        .observeAsState().value?.sortedByDescending { it.isSelected } ?: listOf()
    val userData = loginViewModel.getUserData().asLiveData().observeAsState()

    if (listId.value != -1 && !list.value.any { it.isSelected.orFalse() }){
        navController.navigate(Screens.VocabularyListScreen.name)
    }

    val language = wordViewModel.currentList().observeAsState().value?.language

    if (deleteList) {
        wordViewModel.deleteListById(listId.value)
        wordViewModel.deleteWordsOfList(listId.value)
        wordViewModel.deleteTimeSpentOfList(listId.value)
        wordViewModel.checkIfNoListSelected()
        openBottomSheet = false
        deleteList = false
    }

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
    ) {


        item {
            UserDataView(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 24.dp),
                userDataAndSetting = userData.value,
                loginOnClick = {
                    navController.navigate(Screens.InsertEmailScreen.name + "?skip=${false}")
                },
                editNameClick = {
                    navController.navigate(Screens.NameScreen.name)

                })
        }

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(id = R.string.your_lists),
                    style = fontMedium14(MaterialTheme.colorScheme.onSurfaceVariant)
                )

                CustomButton(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    text = stringResource(id = R.string.add_new),
                    iconStart = painterResource(
                        id = R.drawable.plus_03
                    ),
                    size = ButtonSize.SM,
                    style = ButtonStyle.TextOnly
                ) {
                    navController.navigate(Screens.SelectLanguageScreen.name)
                }

            }

        }

        items(
            list.value.subList(
                0,
                if (list.value.size >= 3) 3
                else list.value.size
            )
        ) {

            ItemList(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 16.dp),
                id = it.id.orZero(),
                title = it.language.orEmpty(),
                subTitle = it.title.orEmpty(),
                tertiaryText = if (it.countOfTableTwoItems.orZero() == 0 || it.countOfTableTwoItems.orZero() == 1) stringResource(
                    id = R.string.word_format,
                    it.countOfTableTwoItems.orZero()
                ) else stringResource(id = R.string.words_format, it.countOfTableTwoItems.orZero()),
                isSelected = it.isSelected.orFalse(),
                onClick = { id ->
                    wordViewModel.selectList(id)
                    wordViewModel.selectList(id)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        calendarViewModel.stopLastTime()
                        calendarViewModel.insertSpendTime(
                            TimeSpent(
                                id = 0,
                                listId = id,
                                date = LocalDate.now().toString(),
                                startUnix = System.currentTimeMillis(),
                                endUnix = null,
                                type = SpendTimeType.Learning.ordinal
                            )
                        )
                    }

                    navController.navigate(Screens.HomeScreen.name) {
                        popUpTo(0)
                    }
                },
                editCallback = { lId ->
                    navController.navigate(Screens.SelectLanguageScreen.name + "?listId=$lId")

                },
                deleteCallback = { lId ->
                    listId.value = lId
                    openBottomSheet = true

                }
            )

        }


        if (list.value.size > 3) {
            items(list.value.subList(3, list.value.size)) {
                AnimatedVisibility(visible = visible) {


                    ItemList(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .padding(horizontal = 16.dp),
                        id = it.id.orZero(),
                        title = it.language.orEmpty(),
                        subTitle = it.title.orEmpty(),
                        tertiaryText = if (it.countOfTableTwoItems.orZero() == 0 || it.countOfTableTwoItems.orZero() == 1) stringResource(
                            id = R.string.word_format,
                            it.countOfTableTwoItems.orZero()
                        ) else stringResource(
                            id = R.string.words_format,
                            it.countOfTableTwoItems.orZero()
                        ),
                        isSelected = it.isSelected.orFalse(),
                        onClick = { id ->
                            wordViewModel.selectList(id)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                calendarViewModel.stopLastTime()
                                calendarViewModel.insertSpendTime(
                                    TimeSpent(
                                        id = 0,
                                        listId = id,
                                        date = LocalDate.now().toString(),
                                        startUnix = System.currentTimeMillis(),
                                        endUnix = null,
                                        type = SpendTimeType.Learning.ordinal
                                    )
                                )
                            }

                            navController.navigate(Screens.HomeScreen.name) {
                                popUpTo(0)
                            }
                        },
                        editCallback = { lId ->
                            navController.navigate(Screens.SelectLanguageScreen.name + "?listId=$lId")

                        },
                        deleteCallback = { lId ->
                            listId.value = lId
                            openBottomSheet = true

                        }
                    )

                }
            }
        }



        item {
            if (list.value.size > 3) {
                CustomButton(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = if (visible) stringResource(id = R.string.show_less) else stringResource(
                        id = R.string.show_more
                    ),
                    style = ButtonStyle.TextOnly,
                    size = ButtonSize.MD
                ) {
                    visible = !visible
                }
            }

            Divider(
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 2.dp
            )

            ItemOption(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.bookmarks)
            ) {
                navController.navigate(Screens.BookmarkScreen.name)
            }

            ItemOption(text = stringResource(id = R.string.settings)) {
                navController.navigate(Screens.SettingScreen.name)
            }

//            if (listOf("German").contains(language)){
//                ItemOption(text = stringResource(id = R.string.download_data)) {
//                    navController.navigate(Screens.DownloadScreen.name)
//                }
//            }

            ItemOption(text = stringResource(R.string.help_and_feedback)) {
                navController.navigate(Screens.HelpAndFeedbackScreen.name)
            }

            ItemOption(text = stringResource(R.string.privacy_policy), divider = false) {
                navController.navigate(Screens.PrivacyPolicyScreen.name)
            }


            Text(
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 64.dp)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.vocabs_for_android_ver),
                style = fontRegular12(MaterialTheme.colorScheme.outline),
                textAlign = TextAlign.Center
            )

        }
    }


    if (openBottomSheet) {
        ModalBottomSheet(onDismissRequest = { openBottomSheet = false }) {

            Column(
                Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.title_delete_list), style = fontMedium14(
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(id = R.string.desc_delete_list),
                    style = fontMedium12(
                        MaterialTheme.colorScheme.outline
                    )
                )

                Row(modifier = Modifier.padding(top = 16.dp)) {
                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        text = stringResource(id = R.string.no),
                        type = ButtonType.Secondary,
                        style = ButtonStyle.Outline,
                        size = ButtonSize.LG
                    ) {
                        openBottomSheet = false
                    }
                    CustomButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        text = stringResource(id = R.string.yes),
                        type = ButtonType.Danger,
                        size = ButtonSize.LG
                    ) {
                        deleteList = true
                    }
                }
            }


        }
    }


}