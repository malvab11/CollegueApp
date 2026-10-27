package com.example.collegueapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp, // Matches text-[28px]
        lineHeight = 32.sp, // Matches leading-tight
        letterSpacing = (-0.015).sp // Matches tracking-[-0.015em]
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp, // Matches text-xl
        lineHeight = 24.sp, // Matches leading-tight
        letterSpacing = (-0.015).sp // Matches tracking-[-0.015em]
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // Matches text-base
        lineHeight = 24.sp // Matches leading-normal
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp, // Matches text-base
        lineHeight = 24.sp // Matches leading-normal
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp, // Matches text-sm
        lineHeight = 20.sp // Matches leading-normal
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp, // Matches text-xs
        lineHeight = 16.sp // Matches bottom nav text
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp, // Matches text-2xl for grades
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    )
)
