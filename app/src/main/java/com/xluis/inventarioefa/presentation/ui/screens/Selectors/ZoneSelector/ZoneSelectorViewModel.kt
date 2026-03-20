package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserLoggedUsername
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetAllZoneListByUserId
import com.xluis.inventarioefa._domain.UseCases.GetZoneArticleById
import com.xluis.inventarioefa._domain.UseCases.MoveArticleToZone
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.utils.YOUR_MOVE_ROOM
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ZoneSelectorViewModel(
    private val getAllZoneListByUserId : GetAllZoneListByUserId,
    private val getZoneArticleById: GetZoneArticleById,
    private val moveArticleToZone: MoveArticleToZone,
    private val getUserLoggedUsername: GetUserLoggedUsername,
    private val hasInternet: Boolean
) : ViewModel() {

    private val _uiState = mutableStateOf(ZoneSelectorUiState())
    val uiState: State<ZoneSelectorUiState> = _uiState


    private val _uiEffect = Channel<ZoneSelectorUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

    private fun updateState(update: ZoneSelectorUiState.() -> ZoneSelectorUiState) {
        _uiState.value = _uiState.value.update()
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
            }
        }
    }

    fun onEvent(event: ZoneSelectorUiEvent) {
        when (event) {
            is ZoneSelectorUiEvent.GetZoneListByUserId -> getZonesByUserId()
            ZoneSelectorUiEvent.NavigateBack -> navigateBack()
            is ZoneSelectorUiEvent.SelectZone -> updateState {
                copy(
                    zoneIdSelected = event.zoneIdSelected,
                    storageTypeZoneSelected = event.storageType
                )
            }

            is ZoneSelectorUiEvent.SaveMoveArticleMove -> saveMoveArticleMove()
            is ZoneSelectorUiEvent.ShowToast -> showToast(event.message)
            is ZoneSelectorUiEvent.GetArticleById -> getArticleByZoneAndArticleId(
                event.zoneId,
                event.articleId
            )

            is ZoneSelectorUiEvent.Initialize ->{
                updateState {
                    copy(
                        zoneIdFromMove = event.zoneIdFromMove,
                        articleIdToMove = event.articleIdToMove,
                        storageTypeFromMove = event.storageTypeFromMove,
                        zoneFromMoveStorageType = event.zoneToMoveStorageType,
                        articleCountToMove = event.articleCountToMove
                    )
                }
            }

            ZoneSelectorUiEvent.DeselectZone -> {
                updateState { copy(
                    zoneIdSelected = null,
                    storageTypeZoneSelected = null
                ) }
            }
        }
    }

    private fun getArticleByZoneAndArticleId(zoneId: String, articleId: String) {
        viewModelScope.launch {
            changeLoadingTo(true)
            val storageType = _uiState.value.storageTypeFromMove ?: return@launch
            getZoneArticleById(zoneId, articleId,storageType)
                .onSuccess { article ->
                    updateState { copy(articleToMove = article?.copy(count = _uiState.value.articleCountToMove)) }
                }
                .onError { error ->
                    showToast(error.message)
                }
            changeLoadingTo(false)
        }
    }


    private fun saveMoveArticleMove() {
        viewModelScope.launch {
            changeLoadingTo(true)

            val state = uiState.value

            val needNet =
                state.storageTypeZoneSelected == StorageType.FIREBASE ||
                        state.zoneFromMoveStorageType == StorageType.FIREBASE

            if (needNet && !hasInternet) {
                changeLoadingTo(false)
                return@launch showToast("Necesitas internet para hacer la operación")
            }

            val article = state.articleToMove
                ?: return@launch changeLoadingTo(false).also {
                    showToast("Artículo no encontrado")
                }

            val zoneTo = state.zoneIdSelected
                ?: return@launch changeLoadingTo(false).also {
                    showToast("Zona destino no seleccionada")
                }

            var userId: String = YOUR_MOVE_ROOM

            if (needNet) {
                userId = state.userId
                    ?: return@launch changeLoadingTo(false).also {
                        showToast("Usuario no identificado")
                    }
            }

            val userName = getUserLoggedUsername(userId).getOrNull() ?: "???"

            moveArticleToZone(
                zoneIdFrom = state.zoneIdFromMove,
                storageFrom = state.zoneFromMoveStorageType ?: StorageType.LOCAL,
                zoneIdTo = zoneTo,
                storageTo = state.storageTypeZoneSelected ?: StorageType.LOCAL,
                article = article,
                userName = userName,
                userId = userId
            )
                .onSuccess {
                    showToast("Movimiento realizado correctamente")
                    navigateBack()
                }
                .onError { error -> showToast(error.message) }

            changeLoadingTo(false)
        }
    }


    private fun getZonesByUserId() {
        viewModelScope.launch {
            changeLoadingTo(true)
            val userId = _uiState.value.userId
            val zoneIdFrom = _uiState.value.zoneIdFromMove ?: return@launch
            val storageType = _uiState.value.storageTypeFromMove ?: return@launch

            getAllZoneListByUserId(userId,zoneIdFrom,storageType)
                .onSuccess {
                    updateState { copy(zoneList = it) }
                }.onError {
                    showToast(it.message)
                }

            changeLoadingTo(false)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.send(ZoneSelectorUiEffect.NavigateBack)
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(ZoneSelectorUiEffect.ShowToast(message))
        }
    }

    private fun changeLoadingTo(loadingState: Boolean) {
        viewModelScope.launch {
            updateState { copy(isLoading = loadingState) }
        }
    }

}