package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun MoveQuantityDialog(
    show: Boolean,
    maxCount: Int,
    dismissDialog: () -> Unit,
    onMove: (Int) -> Unit
) {

    if (!show) return

    var quantity by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = dismissDialog,
        title = { Text("Mover Artículo") },
        text = {
            Column {
                Text("Cantidad máxima a mover: $maxCount")

                Spacer(Modifier.height(8.dp))

                TextField(
                    value = quantity,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.all(Char::isDigit)) {
                            quantity = input
                            errorMessage = null
                        }
                    },
                    label = { Text("Cantidad a mover") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessage != null
                )

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val qty = quantity.toIntOrNull()

                    if (qty != null && qty in 1..maxCount) {
                        onMove(qty)
                        dismissDialog()
                    } else {
                        errorMessage = "Ingrese un valor válido entre 1 y $maxCount"
                    }
                }
            ) {
                Text("Mover")
            }
        },
        dismissButton = {
            TextButton(onClick = dismissDialog) {
                Text("Cancelar")
            }
        }
    )
}