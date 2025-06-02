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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.User.Rol
import com.xluis.inventarioefa.domain.model.DataClass.User.User
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User.UserFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User.UserFirestoreViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.DefaultPasswordTextField
import com.xluis.inventarioefa.utils.DefaultTextField
import com.xluis.inventarioefa.utils.cleanFields
import com.xluis.inventarioefa.utils.isValidEmail
import com.xluis.inventarioefa.utils.isValidPassword
import com.xluis.inventarioefa.utils.toast

@Composable
fun RegisterScreen() {

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelBuilder.getAuthViewModelFactory()
    )
    val userFirestoreViewModel: UserFirestoreViewModel =
        viewModel(factory = UserFirestoreViewModelBuilder.getUserFirestoreViewModelFactory())

    val registerUserResult by authViewModel.registerUserState.collectAsState()
    val registerFirestoreResult by userFirestoreViewModel.insertUserStatus.collectAsState()

    val correctlyRegister = registerUserResult is SuspendResult.Success
            && registerFirestoreResult is SuspendResult.Success

    val context = LocalContext.current

    LaunchedEffect(registerUserResult, registerFirestoreResult) {
        handleErrors(
            registerAuthResult = registerUserResult,
            registerFirestoreResult = registerFirestoreResult,
            showMessage = { message -> context.toast(message) }
        )

        if (correctlyRegister) context.toast("Usuerio registrado correctamente")

        authViewModel.cleanResouerces()
    }

    val authLoading = registerUserResult is SuspendResult.Loading
    val userLoading = registerFirestoreResult is SuspendResult.Loading

    val isLoading = authLoading || userLoading



    ViewScreenContent(
        onRegisterUser = { user ->
            authViewModel.registerUserInFirebaseAuth(user.email, user.password)
            userFirestoreViewModel.insertUserInFirestore(user)
        }
    )
    if (isLoading) DefaultLoadingScreen()

}

@Composable
private fun ViewScreenContent(
    onRegisterUser: (User) -> Unit
) {
    val userName = rememberSaveable { mutableStateOf("") }
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val rol = rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            value = userName.value,
            onTextChange = { newName -> userName.value = newName },
            label = "Nombre de Usuario"
        )

        DefaultDropDownSelector(
            optionList = Rol.entries.map { it.displayName },
            labelText = "Rol",
            onOptionSelected = { rolSelected -> rol.value = rolSelected }
        )


        DefaultTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            value = email.value,
            onTextChange = { newEmail -> email.value = newEmail },
            label = "Email"
        )
        DefaultPasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            passwordValue = password.value,
            onPasswordChange = { newPassword -> password.value = newPassword }
        )

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            contentText = "Registrarse",
            onClick = {
                val user = User(
                    email = email.value,
                    password = password.value,
                    userName = userName.value,
                    rol = rol.value
                )

                if (validateUserAndShowErrors(
                        user = user,
                        showError = { errorMessage -> context.toast(errorMessage) }
                    )
                ) {
                    onRegisterUser(user)
                    listOf(userName, email, password).cleanFields()
                }
            }
        )

    }
}

private fun validateUserAndShowErrors(
    user: User,
    showError: (String) -> Unit
): Boolean {
    if (user.userName.isBlank()) {
        showError("Rellena el nombre de usuario")
        return false
    }
    if (!isValidEmail(user.email)) {
        showError("Email invalido")
        return false
    }
    if (!isValidPassword(user.password)) {
        showError("La contraseña debe tener mínimo 8 caracteres, incluir letras y números")
        return false
    }

    return true
}

private fun handleErrors(
    registerAuthResult: SuspendResult<Boolean>,
    registerFirestoreResult: SuspendResult<Boolean>,
    showMessage: (String) -> Unit
) {
    if (registerAuthResult is SuspendResult.Error) showMessage(registerAuthResult.message)
    if (registerFirestoreResult is SuspendResult.Error) showMessage(registerFirestoreResult.message)
}


