package com.ntg.mywords.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ntg.mywords.model.components.AppbarItem
import com.ntg.mywords.util.orZero
import com.ntg.mywords.ui.theme.FontBold14
import com.ntg.mywords.ui.theme.Secondary100
import com.ntg.mywords.ui.theme.Secondary500
import com.ntg.mywords.ui.theme.Secondary900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appbar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: String = "Appbar",
    titleColor: Color = Secondary900,
    color: Color = Color.White,
    enableNavigation: Boolean = true,
    navigationOnClick:() -> Unit = {},
    navigateIconColor: Color = Secondary500,
    actions: List<AppbarItem> = emptyList(),
    actionOnClick: (Int)  -> Unit= {},
//    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {


    Column {
        TopAppBar(
            title = {
                Text(
                    title,
                    maxLines = 1,
                    style = FontBold14(titleColor)
                )
            },
            navigationIcon = {
                if (enableNavigation){

                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = "navigation",
                            tint = navigateIconColor
                        )
                    }

                }

            },
            actions = {

                actions.forEach {appbarItem ->
                    IconButton(onClick = { actionOnClick.invoke(appbarItem.id) }) {
                        Icon(
                            imageVector = appbarItem.imageVector,
                            tint = appbarItem.iconColor,
                            contentDescription = "action appbar"
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                color
            ),
            scrollBehavior = scrollBehavior
        )

        if (scrollBehavior?.state?.contentOffset.orZero() < -25f){
            Divider(Modifier.height(1.dp), color = Secondary100)

        }

    }

    Log.d("SCROLL ::: ", "${scrollBehavior?.state?.heightOffset} ---- ${scrollBehavior?.state?.contentOffset}")

}