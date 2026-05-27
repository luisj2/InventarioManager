package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa.utils.ValidatedTextField

@Composable
fun ChangeZoneNameDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSaveClick: (String) -> Unit
) {
    if (!show) return

    val zoneName = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Cambiar nombre de la zona"
            )
        },
        text = {
            Column {
                Text("Introduce el nuevo nombre de la zona")

                Spacer(modifier = Modifier.height(12.dp))

                ValidatedTextField(
                    value = zoneName.value,
                    onTextChange = { zoneName.value = it },
                    label = "Nombre de la zona"
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    if (zoneName.value.isNotBlank()) {
                        onSaveClick(zoneName.value.trim())
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}