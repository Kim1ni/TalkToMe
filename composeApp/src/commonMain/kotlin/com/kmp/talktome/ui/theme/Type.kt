package com.kmp.talktome.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import talktome.composeapp.generated.resources.Quicksand_Bold
import talktome.composeapp.generated.resources.Quicksand_Light
import talktome.composeapp.generated.resources.Quicksand_Medium
import talktome.composeapp.generated.resources.Quicksand_Regular
import talktome.composeapp.generated.resources.Quicksand_SemiBold
import talktome.composeapp.generated.resources.Res

val baseline = Typography()

@Composable
fun quickSandFontFamily() = FontFamily(
    Font(Res.font.Quicksand_Light, FontWeight.Light),
    Font(Res.font.Quicksand_Regular, FontWeight.Normal),
    Font(Res.font.Quicksand_SemiBold, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.Quicksand_Medium, FontWeight.Medium),
    Font(Res.font.Quicksand_Bold, FontWeight.Bold)
)

@Composable
fun appTypography() = Typography().run {
    val fontFamily = quickSandFontFamily()
    copy(
        displayLarge = baseline.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = fontFamily)
    )
}