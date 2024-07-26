package com.ntg.vocabs.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.model.data.GermanPronouns
import com.ntg.vocabs.ui.theme.fontMedium12
import com.ntg.vocabs.ui.theme.fontRegular12

@Composable
fun Table(
    modifier: Modifier = Modifier,
    pronouns: List<GermanPronouns>?
//    list: List<String>,
) {


    val column1Weight = .3f // 30%
    val column2Weight = .7f // 70%
//    val column3Weight = .4f // 70%

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            )
    ) {


        Column {


            Row(Modifier) {
                TableCell(text = "pronoun", weight = column1Weight, isTitle = true)
                TableCell(text = "verb", weight = column2Weight, isTitle = true)
//                TableCell(text = "Column 2", weight = column3Weight, isTitle = true)
            }
            pronouns?.forEach {germanVerb ->

                Row(modifier = Modifier
                    .fillMaxWidth()) {
                    TableCell(text = germanVerb.pronoun.orEmpty(), weight = column1Weight)
                    TableCell(text = germanVerb.verb.orEmpty(), weight = column2Weight)
//                    TableCell(text = "Column 2", weight = column3Weight)
                }
            }
        }


    }

}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    isTitle: Boolean = false
) {
    SelectionContainer(modifier = Modifier
        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        .weight(weight)
        .padding(8.dp)) {
        Text(
            text = text,
            Modifier
                .fillMaxWidth(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = if (isTitle) fontMedium12(MaterialTheme.colorScheme.onBackground) else fontRegular12(
                MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}