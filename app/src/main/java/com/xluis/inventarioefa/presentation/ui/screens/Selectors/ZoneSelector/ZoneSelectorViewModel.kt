package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetUserZonesIds
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetZoneListByIdList
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.GetZoneArticleById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.MoveArticleToZone
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZoneList
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.utils.YOUR_MOVEMENT_ROOM
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ZoneSelectorViewModel(
    private val getUserZonesIds: GetUserZonesIds,
    private val getZoneListByIdList: GetZoneListByIdList,
    private val getRoomZoneList: GetRoomZoneList,
    private val getZoneArticleById: GetZoneArticleById,
    private val moveArticleToZone: MoveArticleToZone,
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
                getZonesByUserId()
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

            is ZoneSelectorUiEvent.SaveMoveArticleMovement -> saveMoveArticleMovement()
            is ZoneSelectorUiEvent.ShowToast -> showToast(event.message)
            is ZoneSelectorUiEvent.GetArticleById -> getArticleByZoneAndArticleId(
                event.zoneId,
                event.articleId
            )

            is ZoneSelectorUiEvent.Initialize -> updateState {
                copy(
                    zoneIdFromMove = event.zoneIdFromMove,
                    articleIdToMove = event.articleIdToMove,
                    zoneFromMoveStorageType = event.zoneToMoveStorageType,
                    articleCountToMove = event.articleCountToMove
                )
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
            getZoneArticleById(zoneId, articleId)
                .onSuccess { article ->
                    updateState { copy(articleToMove = article?.copy(count = _uiState.value.articleCountToMove)) }
                }
                .onError { error ->
                    showToast(error.message)
                }
            changeLoadingTo(false)
        }
    }


    private fun saveMoveArticleMovement() {
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

            var userId: String = YOUR_MOVEMENT_ROOM

            if (needNet) {
                userId = state.userId
                    ?: return@launch changeLoadingTo(false).also {
                        showToast("Usuario no identificado")
                    }
            }

            moveArticleToZone(
                zoneIdFrom = state.zoneIdFromMove,
                storageFrom = state.zoneFromMoveStorageType ?: StorageType.LOCAL,
                zoneIdTo = zoneTo,
                storageTo = state.storageTypeZoneSelected ?: StorageType.LOCAL,
                article = article,
                countToMove = state.articleCountToMove,
                userId = userId
            )
                .onSuccess {
                    showToast("Movimiento realizado correctamente")
                }
                .onError { error ->
                    showToast(error.message)
                }

            changeLoadingTo(false)
        }
    }


    private fun getZonesByUserId() {
        viewModelScope.launch {
            changeLoadingTo(true)
            val userId = uiState.value.userId
            var firestoreZones = listOf<Zone>()
            var roomZones = listOf<Zone>()

            if (userId != null) {
                getUserZonesIds(userId)
                    .flatMap { idList ->
                        getZoneListByIdList(idList)
                    }
                    .onSuccess { zoneList ->
                        firestoreZones = zoneList
                    }
                    .onError { error ->
                        showToast(error.message)
                    }
            }

            getRoomZoneList()
                .onSuccess { zoneList ->
                    roomZones = zoneList
                }
                .onError { error ->
                    showToast(error.message)
                }

            updateState { copy(zoneList = firestoreZones + roomZones) }

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