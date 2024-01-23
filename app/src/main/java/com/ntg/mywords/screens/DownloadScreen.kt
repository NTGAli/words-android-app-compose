package com.ntg.mywords.screens

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.gson.Gson
import com.ntg.mywords.R
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.components.Appbar
import com.ntg.mywords.components.DownloadItem
import com.ntg.mywords.model.response.DataRes
import com.ntg.mywords.nav.Screens
import com.ntg.mywords.ui.theme.*
import com.ntg.mywords.util.*
import com.ntg.mywords.vm.DataViewModel
import com.ntg.mywords.vm.WordViewModel
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    navController: NavController,
    dataViewModel: DataViewModel,
    wordViewModel: WordViewModel,
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

    var isGermanNounsDownloaded = wordViewModel.sizeGermanNoun().observeAsState().value


//    LaunchedEffect(key1 = Unit, block = {
//
//        dataViewModel.getDataList("German").observe(owner){
//
//            when(it){
//                is NetworkResult.Error -> {
//
//                }
//                is NetworkResult.Loading -> {
//
//                }
//                is NetworkResult.Success -> {
//                    list = it.data?.data.orEmpty()
//                    list.forEach {
//                        downloadProgress.add(
//                            DownloadState(
//                                title = it.name,
//                                progress = 0
//                            )
//                        )
//                    }
//                }
//            }
//
//        }
//
//    })

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
    })
}

