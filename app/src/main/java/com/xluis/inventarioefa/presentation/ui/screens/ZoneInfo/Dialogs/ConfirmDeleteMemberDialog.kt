package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDeleteMemberDialog(
    show : Boolean,
    memberName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if(!show) return

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Eliminar miembro")
        },
        text = {
            Text(text = "¿Seguro que quieres eliminar a $memberName de la zona?")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() }
            ) {
                Text("Cancelar")
            }
        }
    )
}