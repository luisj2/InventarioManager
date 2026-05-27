package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.AcceptZoneRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.DeleteUserZoneRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserRequests
import com.xluis.inventarioefa._domain.model.User.ZoneRequest
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ZoneRequestsViewModel(
    private val deleteUserZoneRequest: DeleteUserZoneRequest,
    private val acceptZoneRequest: AcceptZoneRequest,
    private val getUserRequests : GetUserRequests
) : ViewModel() {

    private val _uiState = mutableStateOf(ZoneRequestsUiState())
    val uiState: State<ZoneRequestsUiState> = _uiState

    private val _uiEffect = Channel<ZoneRequestsUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

    private fun updateState(update: ZoneRequestsUiState.() -> ZoneRequestsUiState) {
        _uiState.value = _uiState.value.update()
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
            }
        }
    }
    fun onEvent(event: ZoneRequestsUiEvent) {
        when (event) {
            is ZoneRequestsUiEvent.AcceptZoneRequest -> acceptRequest(event.zoneRequest)
            is ZoneRequestsUiEvent.RejectZoneRequest -> rejectZoneRequest(event.requestId)
            ZoneRequestsUiEvent.ChargeUserZoneRequests -> chargeUserZoneRequests()
        }
    }

    private fun chargeUserZoneRequests() {
        viewModelScope.launch {
            val state = _uiState.value
            val userId = state.userId ?: run {
                return@launch
            }

            getUserRequests(userId)
                .onSuccess { updateState { copy(requestsList = it) } }
                .onError { error -> showToast(error.message) }
        }
    }


    private fun rejectZoneRequest(requestId: String) {
        viewModelScope.launch {
            val userId = _uiState.value.userId ?: run {
                showToast("Usuario no identificado")
                return@launch
            }

            updateState { copy(isLoading = true) }

            deleteUserZoneRequest(userId,requestId)
                .onSuccess {
                    showToast("Solicitud eliminada correctamente")
                    chargeUserZoneRequests()
                }
                .onError { error -> showToast(error.message) }

            updateState { copy(isLoading = false) }
        }
    }


        private fun acceptRequest(zoneRequest: ZoneRequest) {
            viewModelScope.launch {

                updateState { copy(isLoading = true) }

                acceptZoneRequest(zoneRequest)
                    .onSuccess {
                        showToast("Solicitud aceptada correctamente")
                        chargeUserZoneRequests()
                    }
                    .onError { error -> showToast(error.message)}

                updateState { copy(isLoading = false) }
            }
        }



    fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(ZoneRequestsUiEffect.showToast(message))
        }
    }
}