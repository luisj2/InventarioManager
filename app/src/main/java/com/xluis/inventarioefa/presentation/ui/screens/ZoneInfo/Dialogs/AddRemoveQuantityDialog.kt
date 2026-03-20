package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddRemoveQuantityDialog(
    show: Boolean,
    maxCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (isAdd: Boolean, quantity: Int) -> Unit
) {
    if (!show) return

    var quantity by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isAdd by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar cantidad") },
        text = {
            Column {
                // Toggle Add / Remove
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Acción: ")
                    Spacer(Modifier.width(8.dp))
                    Row {
                        RadioButton(
                            selected = isAdd,
                            onClick = { isAdd = true }
                        )
                        Text("Añadir")
                        Spacer(Modifier.width(16.dp))
                        RadioButton(
                            selected = !isAdd,
                            onClick = { isAdd = false }
                        )
                        Text("Eliminar")
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (!isAdd) {
                    Text("Cantidad máxima: $maxCount")
                    Spacer(Modifier.height(8.dp))
                }

                TextField(
                    value = quantity,
                    onValueChange = { input ->
                        if (input.isEmpty() || input.all(Char::isDigit)) {
                            quantity = input
                            errorMessage = ""
                        }
                    },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessage.isNotEmpty(),
                    singleLine = true
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val qty = quantity.toIntOrNull() ?: 0

                if (!isAdd) {
                    when {
                        qty < 1 -> errorMessage = "Ingrese un valor mayor que 0"
                        qty > maxCount -> errorMessage = "No puede eliminar más de $maxCount"
                        else -> {
                            onConfirm(isAdd, qty)
                            onDismiss()
                        }
                    }
                } else {
                    if (qty < 1) {
                        errorMessage = "Ingrese un valor mayor que 0"
                    } else {
                        onConfirm(isAdd, qty)
                        onDismiss()
                    }
                }

            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}