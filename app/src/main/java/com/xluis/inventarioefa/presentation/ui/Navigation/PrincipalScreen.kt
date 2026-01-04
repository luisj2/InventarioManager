package com.xluis.inventarioefa.presentation.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class PrincipalScreen(
    val route: Screen,
    val title: String,
    val icon: ImageVector
) {

    Zones(
        route = Screen.ZonePrincipal,
        title = "Zonas",
        icon = Icons.Filled.Place
    ),

    Movements(
        route = Screen.ArticleMovementsRegister,
        title = "Movimientos",
        icon = Icons.Filled.CompareArrows
    ),

    ZoneRequests(
        route = Screen.ZoneRequests,
        title = "Solicitudes Zonas",
        icon = Icons.Filled.List
    ),

    Settings(
        route = Screen.Settings,
        title = "Ajustes",
        icon = Icons.Filled.Settings
    )
}
