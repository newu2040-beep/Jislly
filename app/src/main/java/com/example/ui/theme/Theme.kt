package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeStyle(val displayName: String, val primaryColor: Color, val accentColor: Color) {
    ICY_BLUE("Icy Blue", IcyBlue, Cream),
    BUTTERMILK("Buttermilk", ButtermilkDark, IcyBlue),
    STRAWBERRY("Strawberry Milk", PowderPinkDark, Buttermilk),
    MATCHA("Matcha", MintDark, Cream),
    LAVENDER("Lavender", LavenderDark, PowderPink),
    PEACH_SHERBET("Peach Sherbet", PeachSherbetDark, Cream),
    ESPRESSO_90S("90s Espresso", EspressoBrownDark, Buttermilk),
    MIDNIGHT_NOIR("Midnight Velvet", MidnightAccent, Lavender),
    SAKURA("Sakura Blossom", SakuraPink, PowderPink),
    EMERALD("Emerald Sage", EmeraldForest, Mint),
    SOLAR("Solar Sunset", SolarSunset, SolarAccent),
    VINTAGE_SEPIA("Vintage Sepia", VintageSepiaDark, Buttermilk),
    BABY_BLUE("Baby Blue Pastel", BabyBlueSkyDark, Cream),
    MOCHA_CHAI("Mocha Chai", MochaChaiDark, Buttermilk),
    CHERRY_BLOSSOM("Cherry Blossom", CherryBlossomDark, PowderPink)
}

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

@Composable
fun JisllyTheme(
    themeStyle: AppThemeStyle = AppThemeStyle.ICY_BLUE,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = themeStyle.primaryColor,
            onPrimary = Ink,
            primaryContainer = CharcoalCard,
            onPrimaryContainer = themeStyle.primaryColor,
            secondary = themeStyle.accentColor,
            onSecondary = Ink,
            secondaryContainer = CharcoalSurface,
            onSecondaryContainer = CharcoalText,
            background = CharcoalNavy,
            onBackground = CharcoalText,
            surface = CharcoalSurface,
            onSurface = CharcoalText,
            surfaceVariant = CharcoalCard,
            onSurfaceVariant = CharcoalTextSecondary,
            outline = CharcoalBorder
        )
    } else {
        when (themeStyle) {
            AppThemeStyle.ICY_BLUE -> lightColorScheme(
                primary = IcyBlue,
                onPrimary = PureWhite,
                primaryContainer = Color(0xFFD6E9F8),
                onPrimaryContainer = Ink,
                secondary = PowderPink,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Cream,
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = CreamSoft,
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFE2DACC)
            )
            AppThemeStyle.BUTTERMILK -> lightColorScheme(
                primary = ButtermilkDark,
                onPrimary = Ink,
                primaryContainer = Buttermilk,
                onPrimaryContainer = Ink,
                secondary = IcyBlue,
                onSecondary = PureWhite,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Cream,
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Buttermilk,
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFE5D5AA)
            )
            AppThemeStyle.STRAWBERRY -> lightColorScheme(
                primary = PowderPinkDark,
                onPrimary = PureWhite,
                primaryContainer = PowderPink,
                onPrimaryContainer = Ink,
                secondary = Lavender,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFFF7F9),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = PowderPink,
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFEAC3CD)
            )
            AppThemeStyle.MATCHA -> lightColorScheme(
                primary = MintDark,
                onPrimary = Ink,
                primaryContainer = Mint,
                onPrimaryContainer = Ink,
                secondary = Buttermilk,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFF7FCF8),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Mint,
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFC7E2D0)
            )
            AppThemeStyle.LAVENDER -> lightColorScheme(
                primary = LavenderDark,
                onPrimary = PureWhite,
                primaryContainer = Lavender,
                onPrimaryContainer = Ink,
                secondary = PowderPink,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFAF7FF),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Lavender,
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFDDD2F3)
            )
            AppThemeStyle.PEACH_SHERBET -> lightColorScheme(
                primary = PeachSherbetDark,
                onPrimary = PureWhite,
                primaryContainer = PeachSherbet,
                onPrimaryContainer = Ink,
                secondary = Buttermilk,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFFF8F3),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = PeachSherbet.copy(alpha = 0.4f),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFECC2AB)
            )
            AppThemeStyle.ESPRESSO_90S -> lightColorScheme(
                primary = EspressoBrown,
                onPrimary = PureWhite,
                primaryContainer = Color(0xFFDFCCBB),
                onPrimaryContainer = Ink,
                secondary = Buttermilk,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFAF6F0),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Color(0xFFEADDCF),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFD0BCAB)
            )
            AppThemeStyle.MIDNIGHT_NOIR -> lightColorScheme(
                primary = MidnightAccent,
                onPrimary = PureWhite,
                primaryContainer = Color(0xFFD6D8E8),
                onPrimaryContainer = Ink,
                secondary = Lavender,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFF6F7FB),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Color(0xFFE4E5F2),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFC3C7DF)
            )
            AppThemeStyle.SAKURA -> lightColorScheme(
                primary = SakuraPink,
                onPrimary = PureWhite,
                primaryContainer = SakuraPinkLight,
                onPrimaryContainer = Ink,
                secondary = PowderPink,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFFF5F8),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = SakuraPinkLight,
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFF3BCCF)
            )
            AppThemeStyle.EMERALD -> lightColorScheme(
                primary = EmeraldForest,
                onPrimary = PureWhite,
                primaryContainer = EmeraldLight,
                onPrimaryContainer = Ink,
                secondary = Mint,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFF4F9F9),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = EmeraldLight,
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFADC8C8)
            )
            AppThemeStyle.SOLAR -> lightColorScheme(
                primary = SolarSunset,
                onPrimary = PureWhite,
                primaryContainer = SolarAccent.copy(alpha = 0.4f),
                onPrimaryContainer = Ink,
                secondary = Buttermilk,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFFF7F2),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = SolarAccent.copy(alpha = 0.25f),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFE8B6A2)
            )
            AppThemeStyle.VINTAGE_SEPIA -> lightColorScheme(
                primary = VintageSepiaDark,
                onPrimary = PureWhite,
                primaryContainer = VintageSepia.copy(alpha = 0.35f),
                onPrimaryContainer = Ink,
                secondary = Buttermilk,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFAF6EE),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Color(0xFFEFE8DB),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFD4C2A5)
            )
            AppThemeStyle.BABY_BLUE -> lightColorScheme(
                primary = BabyBlueSkyDark,
                onPrimary = PureWhite,
                primaryContainer = BabyBlueSky.copy(alpha = 0.3f),
                onPrimaryContainer = Ink,
                secondary = Lavender,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFF5F9FE),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Color(0xFFE5F0FA),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFBCD5EC)
            )
            AppThemeStyle.MOCHA_CHAI -> lightColorScheme(
                primary = MochaChaiDark,
                onPrimary = PureWhite,
                primaryContainer = MochaChai.copy(alpha = 0.3f),
                onPrimaryContainer = Ink,
                secondary = Buttermilk,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFBF8F5),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Color(0xFFEFE6DE),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFD7C7BB)
            )
            AppThemeStyle.CHERRY_BLOSSOM -> lightColorScheme(
                primary = CherryBlossomDark,
                onPrimary = PureWhite,
                primaryContainer = CherryBlossomRose.copy(alpha = 0.35f),
                onPrimaryContainer = Ink,
                secondary = PowderPink,
                onSecondary = Ink,
                secondaryContainer = CreamSoft,
                onSecondaryContainer = Ink,
                background = Color(0xFFFFF6F8),
                onBackground = Ink,
                surface = PureWhite,
                onSurface = Ink,
                surfaceVariant = Color(0xFFFDE8EE),
                onSurfaceVariant = InkSecondary,
                outline = Color(0xFFF4BDCE)
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JislllyTypography,
        content = content
    )
}
