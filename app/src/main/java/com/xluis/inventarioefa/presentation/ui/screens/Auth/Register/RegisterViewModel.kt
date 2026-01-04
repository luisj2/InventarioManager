package com.xluis.inventarioefa.presentation.ui.screens.Auth.Register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.RegisterAndSaveUserUseCase
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.DataClass.User.User
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Mapper.toFirestore
import com.xluis.inventarioefa.utils.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerAndSaveUserUseCase: RegisterAndSaveUserUseCase
) : ViewModel() {

    private val _uiState = mutableStateOf(RegisterUiState())
    val uiState: State<RegisterUiState> = _uiState


    private val _uiEffect = Channel<RegisterUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: RegisterUiEvent) {
        when (event) {
            is RegisterUiEvent.EmailChanged -> {
                _uiState.value =
                    _uiState.value.copy(email = event.email)
                validateEmailField()
            }

            is RegisterUiEvent.UserNameChanged -> {
                _uiState.value =
                    _uiState.value.copy(userName = event.userName)
                validateUserNameField()
            }

            is RegisterUiEvent.PasswordChanged -> {
                _uiState.value =
                    _uiState.value.copy(password = event.password)
                validatePasswordField()
            }

            RegisterUiEvent.RegisterClicked -> registerUser()
        }
    }

    private fun registerUser() {
        val state = _uiState.value
        val user = User(email = state.email, userName = state.userName)

        if (!validateUserInput(user, state.password)) return

        viewModelScope.launch {
            _uiState.value = uiState.value.copy(isLoading = true)

            registerAndSaveUserUseCase(user.toFirestore(), state.password)
                .onSuccess {
                    showToast("Usuario registrado correctamente")
                    navigateToLogin()
                }
                .onError { error ->
                    showToast(error.message)
                }


            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private fun validateUserInput(user: User, password: String): Boolean {
        val emailResult = user.validateEmail()
        val userNameResult = user.validateUserName()
        val passwordResult = validatePassword(password)

        _uiState.value = _uiState.value.copy(
            emailError = (emailResult as? ValidationResult.Error)?.message,
            userNameError = (userNameResult as? ValidationResult.Error)?.message,
            passwordError = (passwordResult as? ValidationResult.Error)?.message
        )

        return emailResult is ValidationResult.Success &&
                userNameResult is ValidationResult.Success &&
                passwordResult is ValidationResult.Success
    }

    private fun validateEmailField() {
        val state = uiState.value
        val result = User(email = state.email).validateEmail()
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

    private fun validateUserNameField() {
        val state = _uiState.value
        val result = User(userName = state.userName).validateUserName()

        _uiState.value = state.copy(
            userNameError = (result as? ValidationResult.Error)?.message
        )
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(RegisterUiEffect.ShowToast(message))
        }
    }

    private fun navigateToLogin(){
        viewModelScope.launch {
            _uiEffect.send(RegisterUiEffect.NavigateToLogin)
        }
    }


}