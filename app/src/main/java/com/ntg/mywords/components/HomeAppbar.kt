package com.ntg.mywords.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ntg.mywords.R
import com.ntg.mywords.ui.theme.fontMedium12

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppbar(){

    TopAppBar(
        title = {
            Row(modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .padding(start = 16.dp), text = "search words", style = fontMedium12())
                Icon(modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 16.dp), painter = painterResource(id = R.drawable.microphone_02), contentDescription ="mic")
            }
        },
        actions = {
            Spacer(modifier = Modifier.padding(8.dp))
            Icon(painter = painterResource(id = R.drawable.bell_02), contentDescription = "notifications")

            Spacer(modifier = Modifier.padding(8.dp))

            Box(modifier = Modifier.size(24.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center) {
                Text(modifier = Modifier, text = "A", color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    )
}