package com.example.myapplication.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.unit.sp


val AppTypography = Typography(
    //small dates/day (headercard date + dailypreviewcard day)
                titleSmall = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = outlineLight,
                ),
    //header (icon)
                titleLarge = TextStyle(
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    letterSpacing = 0.sp,
                    hyphens = Hyphens.Auto,
                    color = outlineLightMediumContrast
                ),
    //daily
                bodySmall = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = onSurfaceVariantLight,
                    lineHeight = 15.sp,
                    ),
                bodyMedium = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight(400),
                    color = outlineLightMediumContrast,
                   // lineHeight = 15.sp,
                ),


)
