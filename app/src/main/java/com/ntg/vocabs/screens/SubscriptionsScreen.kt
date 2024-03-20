package com.ntg.vocabs.screens

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.LoadingView
import com.ntg.vocabs.components.TLButton
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.ui.theme.Pro
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium16
import com.ntg.vocabs.ui.theme.fontMedium24
import com.ntg.vocabs.ui.theme.fontRegular12
import com.ntg.vocabs.ui.theme.fontRegular14
import com.ntg.vocabs.util.openInBrowser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController,
            )

        },
        bottomBar = {
            val context = LocalContext.current
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                Divider(
                    Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                TLButton(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = "Buy"
                ) {
                    navController.popBackStack()
                }
                CustomButton(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    text = "continue free ",
                    style = ButtonStyle.TextOnly,
                    size = ButtonSize.LG
                ) {
                    navController.popBackStack()
                }
            }
        }
    )
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    navController: NavController
) {

//    val colors = remember { Animatable(Pro[0]) }
//
//
//    LaunchedEffect(key1 = Unit, block = {
//        repeat(Pro.size){
//            colors.animateTo(Pro[it], animationSpec = tween( 300), animationSpec = infiniteRepeatable(repeatMode = RepeatMode.Reverse))
//        }
//    })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {

            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(
                            16.dp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.padding(16.dp),
                    painter = painterResource(id = R.drawable.icons8_v_2),
                    contentDescription = "logo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

        }

        item {
            TypewriterText(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp),
                texts = listOf("Subscription Plans"), singleText = true
            )
        }

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart))
                {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .padding(horizontal = 16.dp), text = "Free", style = fontMedium16(
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )


                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        text = "Add unlimited Words \n" +
                                "3 Lists\n" +
                                "No Ads\n" +
                                "Auto backup\n" +
                                "One Online Dictionary\n" +
                                "Review",
                        style = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )


                }
            }

        }

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart))
                {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 8.dp)
                            .padding(horizontal = 16.dp), text = "Premium", style = fontMedium16(
                            MaterialTheme.colorScheme.primary
                        )
                    )


                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        text = "One time pay\n" +
                                "Add unlimited Words \n" +
                                "Add unlimited Lists\n" +
                                "No Ads\n" +
                                "Auto backup\n" +
                                "Three Online English Dictionary\n" +
                                "German Dictionary\n" +
                                "Review\n" +
                                "Random Review\n" +
                                "Spend Time\n" +
                                "Add Images for word\n" +
                                "Support Developers",
                        style = fontRegular14(MaterialTheme.colorScheme.onSurfaceVariant)
                    )


                }
            }

            Divider(modifier = Modifier.padding(32.dp), color = Color.Transparent)

        }

    }

}