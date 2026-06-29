package com.reset.feature.home.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reset.feature.home.R

/**
 * Centralised AFK design tokens (the "Sage" palette from the approved design).
 * Screens reference these instead of hardcoding colours, type, or dimensions.
 */

// ── Fonts ─────────────────────────────────────────────────────
object AfkFonts {
    val Display = FontFamily(Font(R.font.michroma_regular))
    val Sans = FontFamily(
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_semibold, FontWeight.SemiBold),
        Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    )
    val Mono = FontFamily(
        Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
        Font(R.font.jetbrains_mono_medium, FontWeight.Medium),
    )
}

// ── Colours ───────────────────────────────────────────────────
object AfkColors {
    val on = Color(0xFF367C6B)
    val onDeep = Color(0xFF265A4F)
    val accent = Color(0xFF52A88F)

    // Text over the gradient surfaces.
    val textPrimary = Color.White
    val textStrong = Color.White.copy(alpha = 0.95f)
    val textSecondary = Color.White.copy(alpha = 0.82f)
    val textTertiary = Color.White.copy(alpha = 0.78f)
    val textMuted = Color.White.copy(alpha = 0.62f)
    val textFaint = Color.White.copy(alpha = 0.60f)

    // Glass surfaces (real blur is API 31+; approximated here with translucency).
    val glassFill = Color.White.copy(alpha = 0.13f)
    val glassBorder = Color.White.copy(alpha = 0.22f)
    val glassActiveFill = Color.White.copy(alpha = 0.95f)

    val bannerScrim = Color(0xFF262E2C).copy(alpha = 0.70f)
    val bannerBorder = Color.White.copy(alpha = 0.18f)

    val arcDecoration = Color.White.copy(alpha = 0.06f)

    // Rest gradient stops (165° in the design; rendered vertically here).
    private val restStops = arrayOf(
        0.00f to Color(0xFF5E7672),
        0.33f to Color(0xFF7BA79C),
        0.64f to Color(0xFFA3C8B8),
        1.00f to Color(0xFFC9E0D0),
    )

    val restGradient: Brush = Brush.linearGradient(
        colorStops = restStops,
        start = Offset.Zero,
        end = Offset(0f, Float.POSITIVE_INFINITY),
    )
}

// ── Shapes ────────────────────────────────────────────────────
object AfkShapes {
    val quote = RoundedCornerShape(18.dp)
    val pill = RoundedCornerShape(percent = 50)
    val banner = RoundedCornerShape(22.dp)
    val bannerButton = RoundedCornerShape(13.dp)
    val notifIcon = RoundedCornerShape(7.dp)
}

// ── Dimensions ────────────────────────────────────────────────
object AfkDimens {
    val screenPaddingH = 24.dp
    val screenPaddingV = 16.dp

    val bubbleSize = 214.dp
    val ringA = 256.dp
    val ringB = 296.dp
    val aura = 272.dp
    val orbitRadius = 142.dp

    val gearSize = 48.dp
    val quotePadding = 16.dp
    val bannerPadding = 16.dp
    val touchTargetMin = 48.dp
}

// ── Type scale ────────────────────────────────────────────────
object AfkType {
    val wordmark = TextStyle(fontFamily = AfkFonts.Display, fontSize = 16.sp, letterSpacing = 2.sp)

    val quoteText = TextStyle(
        fontFamily = AfkFonts.Sans,
        fontSize = 14.5.sp,
        lineHeight = 20.6.sp,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
    )
    val quoteSource = TextStyle(fontFamily = AfkFonts.Mono, fontSize = 10.sp, letterSpacing = 0.5.sp)

    val segValue = TextStyle(fontFamily = AfkFonts.Sans, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
    val segUnit = TextStyle(fontFamily = AfkFonts.Mono, fontSize = 11.sp, fontWeight = FontWeight.Medium)

    val bubbleTop = TextStyle(fontFamily = AfkFonts.Mono, fontSize = 11.sp, letterSpacing = 0.5.sp)
    val bubbleMain = TextStyle(fontFamily = AfkFonts.Sans, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
    val bubbleSub = TextStyle(fontFamily = AfkFonts.Mono, fontSize = 11.sp)

    val footer = TextStyle(fontFamily = AfkFonts.Mono, fontSize = 11.sp)

    val bannerApp = TextStyle(fontFamily = AfkFonts.Mono, fontSize = 11.sp, letterSpacing = 1.sp)
    val bannerTime = TextStyle(fontFamily = AfkFonts.Mono, fontSize = 11.sp)
    val bannerTitle = TextStyle(fontFamily = AfkFonts.Sans, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.2).sp)
    val bannerBody = TextStyle(fontFamily = AfkFonts.Sans, fontSize = 13.5.sp, lineHeight = 18.9.sp)
    val bannerButton = TextStyle(fontFamily = AfkFonts.Sans, fontSize = 14.sp, fontWeight = FontWeight.Bold)

    val errorTitle = TextStyle(fontFamily = AfkFonts.Sans, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    val stubTitle = TextStyle(fontFamily = AfkFonts.Display, fontSize = 16.sp, letterSpacing = 2.sp)
}
