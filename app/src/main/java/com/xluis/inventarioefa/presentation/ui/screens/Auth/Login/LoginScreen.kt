package com.xluis.inventarioefa.presentation.ui.screens.Auth.Login

import RecoverPasswordDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xluis.inventarioefa.R
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.ValidatedPasswordTextField
import com.xluis.inventarioefa.utils.ValidatedTextField
import com.xluis.inventarioefa.utils.toast


@Composable
fun LoginScreen(
    navigateToMainScreen: () -> Unit,
    viewModel: LoginViewModel
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                LoginUiEffect.navigateToMainScreen -> navigateToMainScreen()
                is LoginUiEffect.ShowToast -> context.toast(effect.message)
            }
        }

    }


    if (uiState.isLoading) {
        LoadingIndicator()
    }

    RecoverPasswordDialog(
        show = uiState.showRecoverPasswordDialog,
        email = uiState.recoveryEmail,
        onEmailChange = { email ->
            viewModel.onEvent(LoginUiEvent.RecoveryEmailChanged(email))
        },
        onDismiss = {
            viewModel.onEvent(LoginUiEvent.DismissRecoverPasswordDialog)
        },
        onSendClick = {
            viewModel.onEvent(LoginUiEvent.SendResetEmail)
            viewModel.onEvent(LoginUiEvent.DismissRecoverPasswordDialog)
        },
        isError = uiState.recoveryError != null,
        errorMessage = uiState.recoveryError
    )

    LoginScreenContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )


}
@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onEvent: (event: LoginUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.orden),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        // 🔸 SUBTÍTULO
        Text(
            text = "Organiza tu inventario y olvida tu calvario",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f), // más visible
            style = MaterialTheme.typography.headlineSmall, // más grande que bodyMedium
            fontWeight = FontWeight.SemiBold, // más presencia, pero sin robar protagonismo
            lineHeight = 40.sp // buena separación visual
        )

        Spacer(modifier = Modifier.height(8.dp))
        ValidatedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = uiState.email,
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError,
            onTextChange = { newEmail -> onEvent(LoginUiEvent.EmailChanged(newEmail)) },
            label = "Email"
        )

        ValidatedPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            passwordValue = uiState.password,
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError,
            onPasswordChange = { newPassword -> onEvent(LoginUiEvent.PasswordChanged(newPassword)) }
        )

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            contentText = "Iniciar Sesión",
            onClick = { onEvent(LoginUiEvent.LoginClicked) }
        )

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            contentText = "Entrar sin sesión",
            onClick = {
                onEvent(LoginUiEvent.NavigateToMainScreen)
            }
        )

        Text(
            text = "¿Olvidaste tu contraseña? Pulsa aquí",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable {
                    onEvent(LoginUiEvent.ShowRecoverPasswordDialog)
                }
        )
    }
}
