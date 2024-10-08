package com.ntg.vocabs.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.model.components.PopupItem
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontMedium14
import com.ntg.vocabs.ui.theme.fontMedium16
import com.ntg.vocabs.R

@Composable
fun ItemList(
    modifier: Modifier = Modifier,
    id: Int,
    title: String,
    subTitle: String,
    tertiaryText: String? = null,
    isSelected: Boolean,
    image: ImageVector? = null,
    onClick: (Int) -> Unit,
    editCallback: (Int) -> Unit,
    deleteCallback: (Int) -> Unit
) {


    var showPopup by remember {
        mutableStateOf(false)
    }


    val popupItems = listOf(
        PopupItem(
            id = 0,
            icon = painterResource(id = R.drawable.folder),
            title = stringResource(id = R.string.open_list)
        ),
        PopupItem(
            id = 1,
            icon = painterResource(id = R.drawable.edit_16_1_5),
            title = stringResource(id = R.string.edit)
        ),
        PopupItem(
            id = 2,
            icon = painterResource(id = R.drawable.trash_16_1_5),
            title = stringResource(id = R.string.delete)
        )
    )



    Box(modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .border(
            width = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        )
        .clickable {
            onClick.invoke(id)
        }) {
        Column(modifier = Modifier.align(Alignment.CenterStart))
        {

            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp), text = title, style = fontMedium16(
                    MaterialTheme.colorScheme.primary
                )
            )

            Text(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = if (tertiaryText == null) 16.dp else 8.dp)
                    .padding(horizontal = 16.dp), text = subTitle, style = fontMedium14(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (tertiaryText != null) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    text = tertiaryText,
                    style = fontMedium12(MaterialTheme.colorScheme.outline)
                )
            }

        }


        if (image == null){
            Popup(
                modifier =
                Modifier
                    .align(Alignment.CenterEnd), popupItems = popupItems
            ) {
                when (it) {
                    0 -> {
                        onClick.invoke(id)
                    }
                    1 -> {
                        editCallback.invoke(id)
                    }
                    2 -> {
                        deleteCallback.invoke(id)
                    }
                }
            }
        }else{

        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clickable {
                    onClick.invoke(0)
                },
            imageVector = image,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "more"
        )
        }


    }


}

@Preview(showBackground = true)
@Composable
private fun ItemListPreview() {

    ItemList(
        id = 1,
        title = "English",
        subTitle = "My English List",
        tertiaryText = "250 words",
        isSelected = true,
        onClick = {},
        editCallback = {},
        deleteCallback = {}
    )
}