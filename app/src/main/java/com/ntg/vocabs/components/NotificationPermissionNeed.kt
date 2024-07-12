package com.ntg.vocabs.components

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ntg.vocabs.R
import com.ntg.vocabs.model.components.ButtonSize
import com.ntg.vocabs.model.components.ButtonStyle
import com.ntg.vocabs.model.components.ButtonType
import com.ntg.vocabs.ui.theme.Warning300
import com.ntg.vocabs.ui.theme.Warning900
import com.ntg.vocabs.ui.theme.fontRegular14

@Composable
fun NotificationPermissionNeed(
    modifier: Modifier
) {
    val ctx = LocalContext.current
    Row(
        modifier = modifier
            .background(color = Warning300, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            text = stringResource(
                R.string.notification_permission_off
            ),
            style = fontRegular14(Warning900)
        )

        CustomButton(
            modifier = Modifier.padding(end = 8.dp),
            text = stringResource(id = R.string.grant_permission), size = ButtonSize.XS, style = ButtonStyle.Outline, type = ButtonType.Warning){
            val settingsIntent: Intent =
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, ctx.packageName)
            ctx.startActivity(settingsIntent)
        }
    }
}