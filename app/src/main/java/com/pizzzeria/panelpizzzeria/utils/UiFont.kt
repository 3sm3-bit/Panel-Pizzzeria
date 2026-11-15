/*
 * Copyright (c) 2026 Tayler (montes8). Todos los derechos reservados.
 * Este código es propiedad exclusiva de su autor. Queda prohibida su 
 * copia, distribución o uso sin autorización previa.
 */
package com.pizzzeria.panelpizzzeria.utils

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pizzzeria.panelpizzzeria.R

val textB40: TextStyle
    get() = tayTextBold.headlineLarge
val textB30: TextStyle
    get() = tayTextBold.headlineMedium
val textB28: TextStyle
    get() = tayTextBold.headlineSmall
val textB25: TextStyle
    get() = tayTextBold.titleLarge
val textB22: TextStyle
    get() = tayTextBold.titleMedium
val textB20: TextStyle
    get() = tayTextBold.titleSmall
val textB18: TextStyle
    get() = tayTextBold.bodyLarge
val textB16: TextStyle
    get() = tayTextBold.bodyMedium
val textB14: TextStyle
    get() = tayTextBold.bodySmall
val textB12: TextStyle
    get() = tayTextBold.labelLarge
val textB10: TextStyle
    get() = tayTextBold.labelMedium
val textB8: TextStyle
    get() = tayTextBold.labelSmall
val textS40: TextStyle
    get() = tayTextSemiBold.headlineLarge
val textS35: TextStyle
    get() = tayTextSemiBold.headlineMedium
val textS30: TextStyle
    get() = tayTextSemiBold.headlineSmall
val textS25: TextStyle
    get() = tayTextSemiBold.titleLarge
val textS22: TextStyle
    get() = tayTextSemiBold.titleMedium
val textS20: TextStyle
    get() = tayTextSemiBold.titleSmall
val textS18: TextStyle
    get() = tayTextSemiBold.bodyLarge
val textS16: TextStyle
    get() = tayTextSemiBold.bodyMedium
val textS14: TextStyle
    get() = tayTextSemiBold.bodySmall
val textS12: TextStyle
    get() = tayTextSemiBold.labelLarge
val textS10: TextStyle
    get() = tayTextSemiBold.labelMedium
val textS8: TextStyle
    get() = tayTextSemiBold.labelSmall
val textM40: TextStyle
    get() = tayTextMedium.headlineLarge
val textM35: TextStyle
    get() = tayTextMedium.headlineMedium
val textM30: TextStyle
    get() = tayTextMedium.headlineSmall
val textM25: TextStyle
    get() = tayTextMedium.titleLarge
val textM22: TextStyle
    get() = tayTextMedium.titleMedium
val textM20: TextStyle
    get() = tayTextMedium.titleSmall
val textM18: TextStyle
    get() = tayTextMedium.bodyLarge
val textM16: TextStyle
    get() = tayTextMedium.bodyMedium
val textM14: TextStyle
    get() = tayTextMedium.bodySmall
val textM12: TextStyle
    get() = tayTextMedium.labelLarge
val textM10: TextStyle
    get() = tayTextMedium.labelMedium
val textM8: TextStyle
    get() = tayTextMedium.labelSmall

val textFontBold = FontFamily(Font(R.font.ui_tay_c_bold))
val textFontSemibold = FontFamily(Font(R.font.ui_tay_c_semi_bold))
val textFontMedium = FontFamily(Font(R.font.ui_tay_c_medium))

private val tayTextBold = Typography(
    headlineLarge = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 35.sp,
        color = Color.White
    ),
    headlineSmall = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleSmall = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodySmall = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color.White
    ),
    labelLarge = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color.White
    ),
    labelMedium = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = textFontBold,
        fontWeight = FontWeight.Bold,
        fontSize = 8.sp,
        color = Color.White
    )
)

/////////////////////////////////////////


private val tayTextSemiBold = Typography(
    headlineLarge = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 40.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 35.sp,
        color = Color.White
    ),
    headlineSmall = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 30.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 25.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleSmall = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 20.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 18.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodySmall = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 14.sp,
        color = Color.White
    ),
    labelLarge = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 12.sp,
        color = Color.White
    ),
    labelMedium = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 10.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = textFontSemibold,
        fontSize = 8.sp,
        color = Color.White
    )
)

/////

private val tayTextMedium = Typography(
    headlineLarge = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 40.sp,
        color = Color.White
    ),
    headlineMedium = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 35.sp,
        color = Color.White
    ),
    headlineSmall = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 30.sp,
        color = Color.White
    ),
    titleLarge = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 25.sp,
        color = Color.White
    ),
    titleMedium = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 22.sp,
        color = Color.White
    ),
    titleSmall = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 20.sp,
        color = Color.White
    ),
    bodyLarge = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 18.sp,
        color = Color.White
    ),
    bodyMedium = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 16.sp,
        color = Color.White
    ),
    bodySmall = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 14.sp,
        color = Color.White
    ),
    labelLarge = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 12.sp,
        color = Color.White
    ),
    labelMedium = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 10.sp,
        color = Color.White
    ),
    labelSmall = TextStyle(
        fontFamily = textFontMedium,
        fontSize = 8.sp,
        color = Color.White
    )
)

