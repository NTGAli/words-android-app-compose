package com.ntg.vocabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingView(){

    Box(modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center){

        CircularProgressIndicator(modifier = Modifier
            .progressSemantics()
            .size(24.dp)
            , color = MaterialTheme.colorScheme.onBackground, strokeWidth = 3.dp)

    }

}