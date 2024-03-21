package com.ntg.vocabs.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.Appbar
import com.ntg.vocabs.components.SimpleBtn
import com.ntg.vocabs.components.TextDescriptionAnimated
import com.ntg.vocabs.ui.theme.fontMedium16
import com.ntg.vocabs.util.sendMail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndFeedbackScreen(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Appbar(
                title = stringResource(R.string.help_and_feedback),
                scrollBehavior = scrollBehavior,
                navigationOnClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Content(
                paddingValues = innerPadding,
                navController = navController
            )

        }
    )
}

@Composable
private fun Content(paddingValues: PaddingValues, navController: NavHostController) {

    val ctx = LocalContext.current

    val fq1Desc = "<h2>To Back Up Your Data, Follow These Steps:</h2>\n" +
            "    <ol>\n" +
            "        <li>\n" +
            "            <p><strong>Access Settings:</strong> Navigate to the \"Settings\" section in your app or device.</p>\n" +
            "        </li>\n" +
            "        <li>\n" +
            "            <p><strong>Select Backup and Restore:</strong> In the first section, labeled \"Backup and Restore,\" locate and click on the \"Backup\" option.</p>\n" +
            "        </li>\n" +
            "        <li>\n" +
            "            <p><strong>Choose Your Destination:</strong> Depending on your preference, you have two options:</p>\n" +
            "            <ul>\n" +
            "                <li>\n" +
            "                    <p><strong>Google Drive Backup:</strong> If you wish to backup your data on your Google Drive, you'll need to log in to your Google account.</p>\n" +
            "                </li>\n" +
            "                <li>\n" +
            "                    <p><strong>Phone Backup:</strong> To keep a backup on your device, select 'on phone storage'. Your backup will be saved in the 'Documents' folder.</p>\n" +
            "                </li>\n" +
            "            </ul>\n" +
            "        </li>\n" +
            "    </ol>"


    val fq2Desc = "    <h2>To Restore Your Backup, Follow These Steps:</h2>\n" +
            "    <ol>\n" +
            "        <li>\n" +
            "            <p><strong>Access Settings:</strong> Open the app or device's \"Settings\" menu.</p>\n" +
            "        </li>\n" +
            "        <li>\n" +
            "            <p><strong>Select Backup & Restore:</strong> In the first section, labeled \"Backup & Restore,\" click on the \"Restore\" option.</p>\n" +
            "        </li>\n" +
            "        <li>\n" +
            "            <p><strong>Choose Your Source:</strong></p>\n" +
            "            <ul>\n" +
            "                <li>\n" +
            "                    <p><strong>From Your Google Drive:</strong> If you previously backed up your data on the Google Drive, select the \"On Google Drive\" option to restore it.</p>\n" +
            "                </li>\n" +
            "                <li>\n" +
            "                    <p><strong>From Local Storage:</strong> If you saved your backup on your device or elsewhere, use the \"Import\" option to restore it.</p>\n" +
            "                </li>\n" +
            "            </ul>\n" +
            "        </li>\n" +
            "    </ol>"

    val fq3Desc =
        "<p>If you grant access to your Google Drive, you can select a backup period for your data. Otherwise, there is currently no automatic backup option available.</p>"


    val fQuestions: List<Pair<String, String>> = listOf(
        Pair(
            first = stringResource(R.string.fq1), second = fq1Desc
        ),
        Pair(
            first = stringResource(R.string.fq2), second = fq2Desc
        ),
        Pair(
            first = stringResource(R.string.fq3), second = fq3Desc
        ),
    )


    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = stringResource(id = R.string.frequency_questions),
                style = fontMedium16(MaterialTheme.colorScheme.outline)
            )
        }

        items(fQuestions) {
            TextDescriptionAnimated(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp), text = it.first, description = it.second
            )
        }

        item {
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            SimpleBtn(
                modifier = Modifier.padding(horizontal = 24.dp),
                title = stringResource(id = R.string.send_feedback),
                painter = painterResource(
                    id = R.drawable.message_circle
                )
            ) {
                ctx.sendMail(
                    ctx.getString(R.string.support_email),
                    ctx.getString(R.string.send_feedback)
                )
            }

            SimpleBtn(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 24.dp),
                title = stringResource(id = R.string.report_bug),
                painter = painterResource(
                    id = R.drawable.message_alert_circle
                )
            ) {
                ctx.sendMail(
                    ctx.getString(R.string.support_email),
                    ctx.getString(R.string.report_bug)
                )
            }
        }

    }

}