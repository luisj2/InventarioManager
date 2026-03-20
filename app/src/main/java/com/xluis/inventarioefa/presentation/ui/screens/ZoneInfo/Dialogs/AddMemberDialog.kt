package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xluis.inventarioefa._domain.model.DataClass.User.User
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.utils.ValidatedTextField

@Composable
fun AddMemberDialog(
    show : Boolean,
    onDismiss: () -> Unit,
    onSend: (String) -> Unit
) {

    if(!show) return

    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {

                // Título
                Text(
                    text = "Añadir miembro",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo de email validado
                ValidatedTextField(
                    value = email,
                    onTextChange = {
                        email = it
                        emailError = null
                    },
                    label = "Correo del miembro",
                    isError = emailError != null,
                    errorMessage = emailError,
                    singleLine = true,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {

                    // BOTÓN CANCELAR
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // BOTÓN ENVIAR
                    Button(
                        onClick = {
                            // Validar usando tu lógica de User
                            User(email = email).validateEmail()
                                .onSuccess{ onSend(email) }
                                .onError {
                                    emailError = it.message
                                    return@Button
                                }
                        }
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}
