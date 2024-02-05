package com.ntg.vocabs.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.DownloadItem
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.ui.theme.*
import com.ntg.vocabs.util.*
import com.ntg.vocabs.vm.DataViewModel
import com.ntg.vocabs.vm.WordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    navController: NavController,
    dataViewModel: DataViewModel,
    wordViewModel: WordViewModel,
    enableBottomBar: Boolean
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.dowanloads),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController,
                dataViewModel,
                wordViewModel
            )

        },
        bottomBar = {
            if (enableBottomBar){
                BottomBar{
                    navController.navigate(Screens.HomeScreen.name,
                        NavOptions.Builder()
                            .setPopUpTo(Screens.DownloadScreen.name, inclusive = true)
                            .build()
                    )
                }
            }
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    dataViewModel: DataViewModel,
    wordViewModel: WordViewModel
) {

    var downloadProgress by remember { mutableIntStateOf(0) }
    val isGermanNounsDownloaded = wordViewModel.sizeGermanNoun().observeAsState().value

    var downloadProgressSecondData by remember { mutableIntStateOf(0) }
    val isGermanVerbsDownloaded = wordViewModel.sizeGermanVerbs().observeAsState().value


    val context = LocalContext.current



    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 16.dp), content = {
        
        item{
            DownloadItem(
                modifier = Modifier.padding(top = 8.dp),
                id = 1,
                title = "German",
                subTitle = "articles & plurals",
                tertiaryText = "6MB",
                isSelected = isGermanNounsDownloaded.orZero() != 0,
                isSEnable = isGermanNounsDownloaded.orZero() == 0 || downloadProgress != 0,
                downloadProgress = downloadProgress,
                onClick = {
                    downloadProgress = 1

                    val downloadManagerUtil = DownloadManagerUtil(context)
                    downloadManagerUtil.downloadFile("https://myvocabulary.ntgt.ir/Data/output.json","GAP.json")
                    downloadManagerUtil.setDownloadListener(object : DownloadManagerUtil.DownloadListener {
                        override fun onDownloadProgress(progress: Int) {
//                            downloadProgress.
                            downloadProgress = progress
                        }

                        override fun onDownloadCompleted() {
                            downloadProgress = 100
                            timber("Download successful")
                        }

                        override fun onDownloadFailed() {
                            downloadProgress= -1
                            timber("download failure")
                        }
                    })
                },
            )
        }



        item{
            DownloadItem(
                modifier = Modifier.padding(top = 8.dp),
                id = 1,
                title = "German",
                subTitle = "verbs data",
                tertiaryText = "1MB",
                isSelected = isGermanVerbsDownloaded.orZero() != 0,
                isSEnable = isGermanVerbsDownloaded.orZero() == 0 || downloadProgressSecondData != 0,
                downloadProgress = downloadProgressSecondData,
                onClick = {
                    downloadProgressSecondData = 1

                    val downloadManagerUtil = DownloadManagerUtil(context)
                    downloadManagerUtil.downloadFile("https://myvocabulary.ntgt.ir/Data/combined_data.zip","verb.zip")
                    downloadManagerUtil.setDownloadListener(object : DownloadManagerUtil.DownloadListener {
                        override fun onDownloadProgress(progress: Int) {
//                            downloadProgress.
                            downloadProgressSecondData = progress
                        }

                        override fun onDownloadCompleted() {
                            downloadProgressSecondData = 100
                            timber("Download successful")
                        }

                        override fun onDownloadFailed() {
                            downloadProgressSecondData= -1
                            timber("download failure")
                        }
                    })
                },
            )
        }
    })
}

@Composable
private fun BottomBar(
    onClick:() -> Unit
){
    Column(modifier = Modifier.padding(horizontal = 32.dp).background(MaterialTheme.colorScheme.background)) {
        Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

        CustomButton(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            text = stringResource(id = R.string.skip),
            size = ButtonSize.XL
        ) {
            onClick.invoke()
        }
    }

}


