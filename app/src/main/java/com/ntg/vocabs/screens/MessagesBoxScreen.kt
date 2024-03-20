package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.EmptyWidget
import com.ntg.vocabs.components.MessageItem
import com.ntg.vocabs.model.response.RecentMessage
import com.ntg.vocabs.util.openInBrowser
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.vm.MessageBoxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesBoxScreen(
    navController: NavController,
    messageBoxViewModel: MessageBoxViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.message_box),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(paddingValues = innerPadding, messageBoxViewModel)

        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    messageBoxViewModel: MessageBoxViewModel
) {

    val messages = remember {
        mutableStateListOf<RecentMessage>()
    }

    var isLoading by remember {
        mutableStateOf(true)
    }


    val context = LocalContext.current

    LaunchedEffect(key1 = Unit, block = {
        messageBoxViewModel.loadMessages()
    })
    messageBoxViewModel.messagesLiveData.observeAsState(initial = null).value.let {
        if (it != null) {
            messages.addAll(it.filter { it !in messages })
            isLoading = false
        }
    }

    LazyColumn(modifier = Modifier.padding(paddingValues),
        content = {


            items(messages) {

                MessageItem(
                    title = it.title,
                    description = it.message,
                    action = it.action,
                    actionData = it.link,
                    actionClick = {
                        try {
                            context.openInBrowser(it)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )


            }

            if (messages.isEmpty()){
                item {
                    EmptyWidget(modifier = Modifier.padding(horizontal = 32.dp), title = stringResource(id = R.string.no_message))
                }
            }

        })

    if (isLoading) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }


}