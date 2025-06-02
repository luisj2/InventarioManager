package com.xluis.inventarioefa.presentation.ui.screens.Auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.DefaultPasswordTextField
import com.xluis.inventarioefa.utils.DefaultTextField
import com.xluis.inventarioefa.utils.isValidEmail
import com.xluis.inventarioefa.utils.isValidPassword
import com.xluis.inventarioefa.utils.toast

@Composable
fun LoginScreen(navigateToInventoryData: () -> Unit) {

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelBuilder.getAuthViewModelFactory()
    )

    val context = LocalContext.current

    val loginUserState by authViewModel.logInUserState.collectAsState()
    val isLoading = loginUserState is SuspendResult.Loading

    LaunchedEffect(loginUserState) {
        handleSuccess(
            loginUserState,
            navigateToInventoryData
        )
        handleErrors(
            loginUserState = loginUserState,
            showMessage ={message -> context.toast(message)}
        )
    }

    ViewScreenContent(
        onLoginUser = { email, password ->
            authViewModel.logInFirebaseUser(
                email = email,
                password = password
            )
        }
    )

    if (isLoading) DefaultLoadingScreen()

}

private fun handleSuccess(loginUserState: SuspendResult<Boolean>, navigateToInventoryData: () -> Unit) {
    if(loginUserState is SuspendResult.Success && loginUserState.data) navigateToInventoryData()
}

private fun handleErrors(
    loginUserState: SuspendResult<Boolean>,
    showMessage: (String) -> Unit
) {
    if (loginUserState is SuspendResult.Error) showMessage(loginUserState.message)
}

@Composable
private fun ViewScreenContent(
    onLoginUser: (String, String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        DefaultTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = email,
            onTextChange = { fieldChange -> email = fieldChange },
            label = "Email"
        )

        DefaultPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            passwordValue = password,
            onPasswordChange = { newPassword -> password = newPassword }
        )

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            contentText = "Iniciar Sesión",
            onClick = {
                if (validateLogin(
                        email = email,
                        password = password,
                        showError = { message -> context.toast(message) })
                ) {
                    onLoginUser(email, password)
                }
            }
        )

    }
}

private fun validateLogin(
    email: String,
    password: String,
    showError: (String) -> Unit
): Boolean {
    if (!isValidEmail(email)) {
        showError("Email invalido")
        return false
    }
    if (!isValidPassword(password)) {
        showError("La contraseña debe tener mínimo 8 caracteres, incluir letras y números")
        return false
    }

    return true
}
