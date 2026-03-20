package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun LogoutConfirmationDialog(
    show: Boolean,
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cerrar sesión",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que deseas cerrar sesión?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmLogout
            ) {
                Text(
                    text = "Salir",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(12.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}