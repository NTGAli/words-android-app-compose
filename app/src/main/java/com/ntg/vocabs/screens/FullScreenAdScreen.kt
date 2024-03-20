package com.ntg.vocabs.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ntg.vocabs.R
import com.ntg.vocabs.components.CustomButton
import com.ntg.vocabs.components.ItemList
import com.ntg.vocabs.components.LoadingView
import com.ntg.vocabs.components.TLButton
import com.ntg.vocabs.components.TypewriterText
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.response.FullScreenAd
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.util.openInBrowser
import com.ntg.vocabs.util.timber
import com.ntg.vocabs.vm.MessageBoxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenAdScreen(
    navController: NavController,
    messageBoxViewModel: MessageBoxViewModel
){

    messageBoxViewModel.loadFullScreenAd()
    val ad = messageBoxViewModel.fullScreenAd.observeAsState(initial = null).value

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            if (ad != null){
                Content(
                    paddingValues = innerPadding,
                    navController,
                    ad
                )
            }else{
                LoadingView()
            }

        },
        bottomBar = {
            val context = LocalContext.current
            Column {
                Divider(
                    Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
                TLButton(modifier = Modifier.padding(horizontal = 32.dp), text = ad?.btn_text.orEmpty()){
                    messageBoxViewModel.seenAd(ad?.id.orEmpty(), false)
                    context.openInBrowser(ad?.link.orEmpty())
                    navController.popBackStack()
                }
                CustomButton(modifier = Modifier.padding(horizontal = 32.dp).fillMaxWidth().padding(top = 4.dp),text = "skip", style = ButtonStyle.TextOnly, size = ButtonSize.LG){
                    messageBoxViewModel.seenAd(ad?.id.orEmpty(), true)
                    navController.popBackStack()
                }
            }
        }
    )
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    navController: NavController,
    ad: FullScreenAd
){


    val iconPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(ad.icon)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )

    LazyColumn(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally){

        item {

            if (iconPainter.state is AsyncImagePainter.State.Success) {
                Image(
                    painter = iconPainter,
                    contentDescription = "ads",
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .size(78.dp)
                    ,
                    contentScale = ContentScale.Crop
                )
            }else{
                Box(modifier = Modifier
                    .size(78.dp)
                    .background(color = MaterialTheme.colorScheme.onBackground)
                    .clip(
                        RoundedCornerShape(8.dp)
                    ), contentAlignment = Alignment.Center){
                    Icon(painter = painterResource(id = R.drawable.image), contentDescription = "", tint = MaterialTheme.colorScheme.secondary)
                }
            }
            
        }

        item {
            TypewriterText(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .padding(top = 12.dp),
                texts = listOf(ad.title.orEmpty()), singleText = true)
            
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp),
                text = ad.description.orEmpty(), style = fontMedium14(MaterialTheme.colorScheme.onSurface))


            if (ad.discount.orEmpty().isNotEmpty()){
                ItemList(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(top = 32.dp),
                    id = 1,
                    title = "Discount code",
                    subTitle = ad.discount!!,
                    isSelected = false,
                    image = Icons.Rounded.ContentCopy,
                    onClick = {},
                    editCallback = {},
                    deleteCallback = {}
                )
            }
        }

        item {
            val scrollState = rememberScrollState()

            Row(modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(top = 24.dp, bottom = 32.dp)) {
                repeat(ad.images.orEmpty().size){index ->

                    if (index == 0){
                        Spacer(modifier = Modifier.padding(start = 24.dp))
                    }

                    val screenshot = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ad.images.orEmpty()
                                [index])
                            .size(coil.size.Size.ORIGINAL)
                            .build()
                    )

                    if (screenshot  .state is AsyncImagePainter.State.Success) {
                        Image(
                            painter = screenshot,
                            contentDescription = "ads",
                            modifier = Modifier
                                .width(200.dp)
                                .height(400.dp)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                            ,
                            contentScale = ContentScale.Crop
                        )
                    }else{
                        Box(modifier = Modifier
                            .clip(
                                RoundedCornerShape(8.dp)
                            )
                            .width(200.dp)
                            .height(400.dp)
                            .background(color = MaterialTheme.colorScheme.onBackground)

                            , contentAlignment = Alignment.Center){
                            Icon(painter = painterResource(id = R.drawable.image), contentDescription = "", tint = MaterialTheme.colorScheme.secondary)
                        }
                    }

                }
            }
        }

    }


}