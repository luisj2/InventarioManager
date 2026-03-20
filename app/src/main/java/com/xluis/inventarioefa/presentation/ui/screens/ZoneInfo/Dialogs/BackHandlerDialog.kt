package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmExitDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Salir sin guardar") },
        text = { Text("Tienes cambios sin guardar. ¿Seguro que quieres salir?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Salir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}