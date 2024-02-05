package com.ntg.vocabs.model.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.ntg.vocabs.ui.theme.Secondary500

data class AppbarItem (
    val id: Int,
    val imageVector: ImageVector,
    val iconColor: Color = Secondary500,
        )