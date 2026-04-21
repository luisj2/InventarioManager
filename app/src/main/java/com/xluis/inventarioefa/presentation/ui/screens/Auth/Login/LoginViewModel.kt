package com.xluis.inventarioefa.presentation.ui.screens.Auth.Login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.LoginUserUseCase
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.SendPasswordResetEmail
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.DataClass.User.User
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa._domain.util.toBoolean
import com.xluis.inventarioefa.utils.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUserUseCase: LoginUserUseCase,
    private val sendPasswordResetEmail: SendPasswordResetEmail
) : ViewModel() {

    private val _uiState = mutableStateOf(LoginUiState())
    val uiState: State<LoginUiState> = _uiState

    private val _uiEffect = Channel<LoginUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

    private fun updateState(update: LoginUiState.() -> LoginUiState) {
        _uiState.value = _uiState.value.update()
    }

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> {
                _uiState.value = uiState.value.copy(email = event.email)
                validateEmailField()
            }

            is LoginUiEvent.PasswordChanged -> {
                _uiState.value =
                    uiState.value.copy(password = event.password)
                validatePasswordField()
            }

            LoginUiEvent.LoginClicked -> loginUser()
            LoginUiEvent.ShowRecoverPasswordDialog -> updateState { copy(showRecoverPasswordDialog = true) }
            LoginUiEvent.DismissRecoverPasswordDialog -> updateState { copy(showRecoverPasswordDialog = false) }
            is LoginUiEvent.RecoveryEmailChanged -> {
                updateState { copy(recoveryEmail = event.email) }

                when(val validateEmail = User(email = _uiState.value.recoveryEmail).validateEmail()){
                    is ValidationResult.Error -> updateState { copy(recoveryError = validateEmail.message) }
                    ValidationResult.Success -> updateState { copy(recoveryError = null) }
                }

            }
            LoginUiEvent.SendResetEmail -> sendResetEmail()
        }
    }

    private fun sendResetEmail() {
        val state = _uiState.value
        val recoveryEmail = state.recoveryEmail
        val isValidEmail = User(email = recoveryEmail).validateEmail().toBoolean()

        if(!isValidEmail) return

        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true) }
                sendPasswordResetEmail(recoveryEmail)
                    .onSuccess {
                        showToast("Se ha mandado el correo correctamente")
                    }
                    .onError { error -> showToast(error.message) }
            } finally {
                updateState {
                    copy(
                        isLoading = false,
                        showRecoverPasswordDialog = false,
                        recoveryEmail = "",
                        recoveryError = null
                    )
                }
            }

        }

    }

    private fun loginUser() {
        val state = uiState.value
        val user = User(
            email = state.email
        )

        if (!validateUserInput(user.validateEmail(), state.password)) return

        viewModelScope.launch {
            turnLoadingTo(true)

            loginUserUseCase(state.email, state.password)
                .onSuccess {
                    _uiEffect.send(LoginUiEffect.LoginSuccessful)
                }
                .onError { error ->
                    showToast(error.message)
                }

            turnLoadingTo(false)
        }
    }


    private fun validateUserInput(
        emailValidation: ValidationResult,
        password: String
    ): Boolean {

        val passwordValidation = validatePassword(password)
        _uiState.value = _uiState.value.copy(
            emailError = (emailValidation as? ValidationResult.Error)?.message,
            passwordError = (passwordValidation as? ValidationResult.Error)?.message
        )

        return emailValidation is ValidationResult.Success && passwordValidation is ValidationResult.Success
    }

    private fun validateEmailField() {
        val state = uiState.value
        val user = User(email = state.email)
        val result = user.validateEmail()
        _uiState.value = state.copy(
            emailError = (result as? ValidationResult.Error)?.message
        )
    }

    private fun validatePasswordField() {
        val state = uiState.value
        val result = validatePassword(state.password)
        _uiState.value = state.copy(
            passwordError = (result as? ValidationResult.Error)?.message
        )
    }

    fun setCredentials(email: String, password: String) {
        updateState {
            copy(
                email = email,
                password = password
            )
        }
    }


    private fun turnLoadingTo(loadingState: Boolean) {
        _uiState.value = _uiState.value.copy(isLoading = loadingState)
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(LoginUiEffect.ShowToast(message))
        }
    }
}