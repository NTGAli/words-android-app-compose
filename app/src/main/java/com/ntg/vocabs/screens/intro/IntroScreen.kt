package com.ntg.vocabs.screens.intro

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.TLButton
import com.ntg.vocabs.components.TextChange
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.components.WormIndicator
import com.ntg.vocabs.ui.theme.Danger500
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium24
import com.ntg.vocabs.vm.LoginViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {

    val icons = listOf(
        painterResource(id = R.drawable.v_72),
        painterResource(R.drawable.infinity_1),
        painterResource(R.drawable.time_1),
        painterResource(R.drawable.no_ads),
        painterResource(R.drawable.survey_1),
        painterResource(R.drawable.drive_72),
    )

    val titles = listOf(
        "Vocabs",
        "Limitless",
        "Track Your Progress",
        "Ad-Free Experience",
        "Review",
        "Google Drive Backup",
    )


    val descriptions = listOf(
        "Vocabs, The Ultimate Vocabulary Companion",
        "Add an unlimited number of words to your vocabulary lists.",
        "Monitor the time spent on each list and during reviews to enhance your learning journey.",
        "Say goodbye to interruptions - Vocabs is completely ad-free.",
        "Efficiently review and reinforce your vocabulary.",
        "Securely backup your data to Google Drive automatically. Peace of mind included.",
    )


    val currentTitle = remember {
        mutableStateOf(titles.first())
    }

    val pagerState = rememberPagerState(pageCount = {
        6
    })



    currentTitle.value = titles[pagerState.currentPage]
    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {

            HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { index ->

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        modifier = Modifier.padding(top = 160.dp),
                        painter = icons[index], contentDescription = null,
                        tint = if (index != 3) MaterialTheme.colorScheme.primary else Danger500
                    )

                }

            }



            Column(
                modifier = Modifier
                    .padding(top = 260.dp)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter), horizontalAlignment = Alignment.CenterHorizontally
            ) {



                if (pagerState.currentPage == 0){
                    TypewriterText(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        texts = listOf(
                            "Hello",
                            "Bonjour",
                            "こんにちは",
                            "Hola",
                            "Hallo",
                            "Ciao",
                            "مرحبًا",
                        ),
                        cursor = "\uD83D\uDC4B",
                        delayTime = 1500
                    )
                }else{
                    TypewriterText(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        texts = listOf(
                            titles[pagerState.currentPage]
                        ),
                        singleText = true
                    )
                }

                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 32.dp),
                    text = descriptions[pagerState.currentPage],
                    style = fontMedium14(MaterialTheme.colorScheme.outline),
                    textAlign = TextAlign.Center,
                )

                WormIndicator(count = 6, pagerState = pagerState)
            }

            TLButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .padding(horizontal = 32.dp),
                text = stringResource(id = R.string.start_vocabs)) {
                loginViewModel.setFinishIntro(true)
            }
        }
    }


}

