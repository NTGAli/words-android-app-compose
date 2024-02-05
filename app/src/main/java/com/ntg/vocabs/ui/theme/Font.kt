package com.ntg.vocabs.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ntg.vocabs.R

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

fun fontRegular12(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    color = color
)



fun fontRegular14(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = color
)

// Medium

fun fontMedium10(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    color = color
)

fun fontMedium12(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    color = color
)


fun fontMedium14(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    color = color
)

fun fontMedium16(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    color = color
)

fun fontMedium24(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp,
    color = color
)

// Bold

fun FontBold10(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 10.sp,
    color = color
)

fun fontBold12(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    color = color
)


fun fontBold14(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    color = color
)

fun fontBold24(color: Color = Secondary100) = TextStyle(
    fontFamily = mFont,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    color = color
)
