package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.MessageItem
import com.ntg.vocabs.model.response.RecentMessage
import com.ntg.vocabs.vm.MessageBoxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesBoxScreen(
    navController: NavController,
    messageBoxViewModel: MessageBoxViewModel
){
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
){

    val messages = remember {
        mutableStateListOf<RecentMessage>()
    }

    val owner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit, block ={

        messageBoxViewModel.getMessages().observe(owner){
            when(it){
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading -> {

                }
                is NetworkResult.Success -> {
                    if (it.data?.data.orEmpty().isNotEmpty()){
                        messages.addAll(it.data?.data.orEmpty().toList())
                    }
                }
            }
        }

    })

    LazyColumn(modifier = Modifier.padding(paddingValues),
        content = {


        items(messages){

            MessageItem(
                title = it.title,
                description = it.description,
                action = it.button,
                actionData = it.link,
                actionClick = {}
            )


        }

    })


    
}