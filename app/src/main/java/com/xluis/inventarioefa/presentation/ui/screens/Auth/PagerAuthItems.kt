package com.xluis.inventarioefa.presentation.ui.screens.Auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class PagerAuthItems (
    val selectedIcon : ImageVector,
    val unselectedIcon : ImageVector,
    val text : String
) {
    Login(
        Icons.Outlined.Person,
        Icons.Filled.Person,
        "Iniciar Sesión"
    ),

    Register(
        Icons.Outlined.Edit,
        Icons.Filled.Edit,
        "Registrarse"
    )
}