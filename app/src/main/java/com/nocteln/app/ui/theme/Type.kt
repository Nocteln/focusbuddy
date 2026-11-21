package com.nocteln.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Police par défaut pour l'instant.
// Plus tard : ajoute Space Grotesk (titres) et Inter (texte) dans res/font,
// puis remplace FontFamily.Default par ta propre FontFamily.
private val PoliceTitre = FontFamily.Default
private val PoliceTexte = FontFamily.Default

val Typography = Typography(
    // Gros chrono de l'écran de session (ex. 24:18)
    displayLarge = TextStyle(
        fontFamily = PoliceTitre,
        fontWeight = FontWeight.Bold,
        fontSize = 68.sp,
        lineHeight = 68.sp,
        letterSpacing = (-1).sp
    ),
    // Titre d'écran de fin de session ("Belle récolte !")
    headlineLarge = TextStyle(
        fontFamily = PoliceTitre,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp
    ),
    // Titre d'écran ("Boutique", "Mon jardin")
    titleLarge = TextStyle(
        fontFamily = PoliceTitre,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    // Titre de section
    titleMedium = TextStyle(
        fontFamily = PoliceTexte,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PoliceTexte,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PoliceTexte,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Texte des boutons
    labelLarge = TextStyle(
        fontFamily = PoliceTexte,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    // Petites étiquettes (barre de navigation, pastilles)
    labelSmall = TextStyle(
        fontFamily = PoliceTexte,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)