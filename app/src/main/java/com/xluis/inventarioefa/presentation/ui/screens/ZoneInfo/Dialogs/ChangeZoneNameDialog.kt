package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.ValidatedTextField

@Composable
fun ChangeZoneNameDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onSaveClick: (String) -> Unit
) {
    if (!show) return

    // Estado local para el nombre
    val zoneName = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar nombre de la zona") },
        text = {
            Column {
                Text("Introduce el nuevo nombre de la zona")

                Spacer(modifier = Modifier.height(8.dp))

                ValidatedTextField(
                    value = zoneName.value,
                    onTextChange = { zoneName.value = it },
                    label = "Nombre de la zona"
                )
            }
        },
        confirmButton = {
            DefaultButton(
                onClick = { onSaveClick(zoneName.value) },
                contentText = "Guardar"
            )
        },
        dismissButton = {
            Text(
                text = "Cancelar",
                modifier = Modifier.clickable { onDismiss() },
                color = MaterialTheme.colorScheme.primary
            )
        }
    )
}