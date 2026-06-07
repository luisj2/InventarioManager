package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialogByDescription(
    show : Boolean,
    articleDescription: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    if(!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Confirmar eliminación")
        },
        text = {
            Text(
                "¿Desea eliminar el artículo con la descripción ${
                    if (articleDescription.isBlank()) {
                        "vacía"
                    } else {
                        "\"$articleDescription\""
                    }
                }?")},
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}