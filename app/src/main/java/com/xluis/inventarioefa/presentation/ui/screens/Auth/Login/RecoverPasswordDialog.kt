
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.ValidatedTextField

@Composable
fun RecoverPasswordDialog(
    show : Boolean,
    email: String,
    onEmailChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSendClick: () -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    if(!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Recuperar contraseña")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Introduce tu correo para recibir el enlace de recuperación")

                ValidatedTextField(
                    value = email,
                    onTextChange = onEmailChange,
                    isError = isError,
                    errorMessage = errorMessage,
                    label = "Email"
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DefaultButton(
                    onClick = onSendClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentText = "Enviar correo"
                )

                Text(
                    text = "Cancelar",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onDismiss() }
                )
            }
        },
        dismissButton = {},
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 8.dp
    )
}