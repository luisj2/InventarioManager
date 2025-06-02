package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Result.UiEvent
import com.xluis.inventarioefa.domain.model.Database.Firebase.Auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val isLoggedIn: Boolean get() = repository.isUserLoggedIn()

    private val _registerUserState =
        MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val registerUserState: StateFlow<SuspendResult<Boolean>> = _registerUserState

    private val _logInUserState =
        MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val logInUserState: StateFlow<SuspendResult<Boolean>> = _logInUserState

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun registerUserInFirebaseAuth(email: String, password: String) {
        viewModelScope.launch {
            _registerUserState.value = SuspendResult.Loading
            _registerUserState.value = repository.registerUser(email, password)
        }
    }

    fun logInFirebaseUser(email: String, password: String) {
        viewModelScope.launch {
            _logInUserState.value = SuspendResult.Loading
            _logInUserState.value = repository.logIn(email, password)
        }
    }

    fun getLoggedUserEmail(): String? = repository.getLoggedUserEmail()

    fun logOut() {
        repository.logOut()
    }

    fun cleanResouerces(){
        _registerUserState.value = SuspendResult.Idle
        _logInUserState.value = SuspendResult.Idle
    }
}
