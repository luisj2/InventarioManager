package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.IsUserLoggedIn
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.Logout
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserLoggedUsername
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val isUserLoggedIn: IsUserLoggedIn,
    private val getUserLoggedUsername: GetUserLoggedUsername,
    private val logout: Logout
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    private val _uiEffect = Channel<SettingUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private fun updateState(update: SettingsUiState.() -> SettingsUiState) {
        _uiState.value = _uiState.value.update()
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
            }
        }
    }

    fun onEvent(event: SettingsUiEvent) {
        when (event) {
            SettingsUiEvent.InitSession -> initSession()
            SettingsUiEvent.OnLoginClicked -> onLoginClicked()
            SettingsUiEvent.OnLogoutClicked -> logOutClick()
            SettingsUiEvent.NavigateToLogin -> navigateToLogin()
            SettingsUiEvent.DissmissLogoutDialog -> updateState { copy(showConfirmLogoutDialog = false) }
            SettingsUiEvent.ShowConfirmLogoutDialog -> updateState { copy(showConfirmLogoutDialog = true) }
        }
    }

    private fun logOutClick() {
        viewModelScope.launch {
            logout()
            navigateToLogin()
        }
    }

    private fun onLoginClicked() {
        viewModelScope.launch {
            _uiEffect.send(SettingUiEffect.NavigateToLogin)
        }
    }

    private fun initSession() {
        val isLoggedIn = isUserLoggedIn()

        // Si no hay userId, actualiza el estado y sal
        val userId = _uiState.value.userId

        if (userId == null) {
            updateState { copy(isLoggedIn = isLoggedIn, username = null) }
            return
        }

        // Lanza la coroutine para obtener el username
        viewModelScope.launch {
            // Indica que estamos cargando
            updateState { copy(isLoading = true) }

            getUserLoggedUsername(userId)
                .onSuccess { username ->
                    // Actualiza estado con username recibido
                    updateState {
                        copy(
                            isLoggedIn = isLoggedIn,
                            username = username,
                            isLoading = false
                        )
                    }
                }
                .onError { error ->
                    // Muestra toast y actualiza estado con username null
                    showToast(error.message)
                    updateState {
                        copy(
                            isLoggedIn = isLoggedIn,
                            username = null,
                            isLoading = false
                        )
                    }
                }
        }
    }


    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(SettingUiEffect.ShowToast(message))
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            _uiEffect.send(SettingUiEffect.NavigateToLogin)
        }
    }
}