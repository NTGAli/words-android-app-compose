package com.ntg.mywords.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ntg.mywords.R

val mFont = FontFamily(
    Font(R.font.outfilt_regular),
    Font(R.font.outfilt_regular, FontWeight.Normal),
    Font(R.font.outfilt_medium, FontWeight.Medium),
    Font(R.font.outfilt_thin, FontWeight.Thin),
    Font(R.font.outfilt_bold, FontWeight.Bold),
    Font(R.font.outfilt_extra_light, FontWeight.ExtraLight),
    Font(R.font.outfilt_black, FontWeight.Black),
    Font(R.font.outfilt_extra_bold, FontWeight.ExtraBold),
    Font(R.font.outfilt_semi_bold, FontWeight.SemiBold),
)

// Regular
fun FontRegular10(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 10.sp,
    color = color
)

fun FontRegular12(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    color = color
)



fun FontRegular14(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = color
)

// Medium

fun FontMedium10(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    color = color
)

fun FontMedium12(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    color = color
)


fun FontMedium14(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    color = color
)

fun FontMedium16(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    color = color
)

// Bold

fun FontBold10(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    color = color
)

fun FontBold12(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    color = color
)


fun FontBold14(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    color = color
)
