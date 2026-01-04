package com.xluis.inventarioefa.presentation.ui.screens.Auth.Register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.R
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.ValidatedPasswordTextField
import com.xluis.inventarioefa.utils.ValidatedTextField
import com.xluis.inventarioefa.utils.toast

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin : () -> Unit
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is RegisterUiEffect.ShowToast -> context.toast(effect.message)
                RegisterUiEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    }
    RegisterScreenContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )
}

@Composable
private fun RegisterScreenContent(
    uiState: RegisterUiState,
    onEvent: (event: RegisterUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.person),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        // 🔸 SUBTÍTULO
        Text(
            text = "¿Preparado para encontrar sin buscar?",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f), // más visible
            style = MaterialTheme.typography.headlineSmall, // más grande que bodyMedium
            fontWeight = FontWeight.SemiBold, // más presencia, pero sin robar protagonismo
            lineHeight = 40.sp // buena separación visual
        )

        Spacer(modifier = Modifier.height(10.dp))

        ValidatedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            value = uiState.userName,
            onTextChange = { newName -> onEvent(RegisterUiEvent.UserNameChanged(newName)) },
            isError = uiState.userNameError != null,
            errorMessage = uiState.userNameError,
            label = "Nombre de Usuario"
        )



        ValidatedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            value = uiState.email,
            onTextChange = { newEmail -> onEvent(RegisterUiEvent.EmailChanged(newEmail)) },
            isError = uiState.emailError != null,
            errorMessage = uiState.emailError,
            label = "Email"
        )

        ValidatedPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            passwordValue = uiState.password,
            isError = uiState.passwordError != null,
            errorMessage = uiState.passwordError    ,
            onPasswordChange = { newPassword -> onEvent(RegisterUiEvent.PasswordChanged(newPassword)) }
        )

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            contentText = "Registrarse",
            onClick = { onEvent(RegisterUiEvent.RegisterClicked) }
        )

    }
}






