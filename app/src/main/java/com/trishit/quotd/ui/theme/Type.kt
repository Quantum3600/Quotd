package com.trishit.quotd.ui.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.trishit.quotd.FunnelDisplayFamily
import com.trishit.quotd.MuseoModernoFamily

// Set of Material typography styles to start with
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MuseoModernoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MuseoModernoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    headlineMediumEmphasized = TextStyle(
        fontFamily = FunnelDisplayFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.15.sp
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)