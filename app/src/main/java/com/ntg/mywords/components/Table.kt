package com.ntg.mywords.components

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ntg.mywords.ui.theme.fontMedium12
import com.ntg.mywords.ui.theme.fontMedium16
import com.ntg.mywords.ui.theme.fontRegular12

@Composable
fun Table(
    modifier: Modifier = Modifier,
//    list: List<String>,
){

    val list = listOf(1,2,3,4,5,6)

    val column1Weight = .2f // 30%
    val column2Weight = .4f // 70%
    val column3Weight = .4f // 70%

    Box(modifier = modifier
        .fillMaxWidth()
        .clip(shape = RoundedCornerShape(16.dp))
        .wrapContentHeight()
        .border(
            width = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )) {



        Column {


            Row(Modifier) {
                TableCell(text = "Column 1", weight = column1Weight, isTitle = true)
                TableCell(text = "Column 2", weight = column2Weight, isTitle = true)
                TableCell(text = "Column 2", weight = column3Weight, isTitle = true)
            }
            list.forEach {

                Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                    TableCell(text = "Column 1", weight = column1Weight)
                    TableCell(text = "Column 2", weight = column2Weight)
                    TableCell(text = "Column 2", weight = column3Weight)
//                    Text(modifier= Modifier.weight(column1Weight),text = "Case", style = fontMedium16(MaterialTheme.colorScheme.outline))
//                    Text(modifier= Modifier.weight(column2Weight),text = "Singular", style = fontMedium16(MaterialTheme.colorScheme.outline))
//                    Text(modifier= Modifier.weight(column3Weight),text = "Plural", style = fontMedium16(MaterialTheme.colorScheme.outline))

//                    if (index > )

            }
//                Divider(
//                    modifier
//                        .fillMaxWidth()
//                        .height(1.dp)
//                        .background(MaterialTheme.colorScheme.outlineVariant))
            }
        }




    }

}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isTitle:Boolean = false
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .fillMaxHeight()
            .weight(weight)
            .padding(8.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = if (isTitle) fontMedium12(MaterialTheme.colorScheme.onBackground) else fontRegular12(MaterialTheme.colorScheme.onSurfaceVariant)
    )
}