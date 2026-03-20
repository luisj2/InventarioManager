package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteDialog(
    show : Boolean,
    articleCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    if(!show) return

    val articleText = if (articleCount == 1) "artículo" else "artículos"
    val message = "¿Seguro que quieres eliminar $articleCount $articleText? Esta acción no se puede deshacer."

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar artículos") },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}