package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.IsUserLoggedIn
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetAllZoneList
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.RemoveZoneListCase
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.utils.Filters.filterZones
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class ZonePrincipalViewModel(
    private val getAllZoneList: GetAllZoneList,
    private val isUserLoggedIn: IsUserLoggedIn,
    private val removeZoneListCase: RemoveZoneListCase
) : ViewModel() {

    private val _uiState = mutableStateOf(ZonePrincipalUiState())
    val uiState: State<ZonePrincipalUiState> = _uiState

    private val _uiEffect = Channel<ZonePrincipalUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()


    private fun updateState(update: ZonePrincipalUiState.() -> ZonePrincipalUiState) {
        _uiState.value = _uiState.value.update()
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
                callAndUpdateUserZoneList()
            }
        }
    }

    fun onEvent(event: ZonePrincipalUiEvent) {
        when (event) {
            is ZonePrincipalUiEvent.NavigateToCreateFirebaseZone -> {
                viewModelScope.launch {
                    val userId = _uiState.value.userId
                    if (userId != null && isUserLoggedIn()) {
                        _uiEffect.send(
                            ZonePrincipalUiEffect.NavigateToCreateZone(
                                event.parentId,
                                StorageType.FIREBASE
                            )
                        )
                    } else {
                        showToast("Inicia sesión para crear una zona compartida")
                        _uiEffect.send(ZonePrincipalUiEffect.NavigateToSettingScreen)
                    }
                }
            }

            is ZonePrincipalUiEvent.NavigateToCreateLocalZone -> {
                viewModelScope.launch {
                    _uiEffect.send(
                        ZonePrincipalUiEffect.NavigateToCreateZone(
                            event.parentId,
                            StorageType.LOCAL
                        )
                    )
                }
            }

            is ZonePrincipalUiEvent.NavigateToZoneInfo -> {
                viewModelScope.launch {
                    _uiEffect.send(
                        ZonePrincipalUiEffect.NavigateToZoneInfo(
                            event.zoneId,
                            event.storageType
                        )
                    )
                }
            }

            ZonePrincipalUiEvent.CloseCreateZoneMenu -> _uiState.value =
                _uiState.value.copy(isCreateZoneMenuOpen = false)

            ZonePrincipalUiEvent.OpenCreateZoneMenu -> _uiState.value =
                _uiState.value.copy(isCreateZoneMenuOpen = true)

            is ZonePrincipalUiEvent.UpdateUserZoneList -> {
                callAndUpdateUserZoneList()
            }


            ZonePrincipalUiEvent.ActivateSelectZoneToRemoveMode -> _uiState.value =
                _uiState.value.copy(selectionMode = true)

            ZonePrincipalUiEvent.DesactivateSelectZoneToRemoveMode -> {
                _uiState.value =
                    _uiState.value.copy(selectionMode = false, selectionToRemoveZones = emptySet())
            }

            ZonePrincipalUiEvent.DeleteSelectedZones -> deleteSelectedZones()
            is ZonePrincipalUiEvent.ToggleZoneDeleteSelection -> {
                val zone = event.zone
                val currentList = _uiState.value.selectionToRemoveZones.toMutableList()

                // Función recursiva para obtener todos los hijos de una zona
                fun getAllChildren(zone: Zone): List<Zone> {
                    val children: List<Zone> = zone.childIdList
                        ?.mapNotNull { childId -> _uiState.value.allZoneList.find { it.id == childId } }
                        ?: emptyList()

                    val descendants: List<Zone> = children.flatMap { getAllChildren(it) }

                    return children + descendants
                }

                // Creamos lista del padre + todos sus hijos
                val zonesToToggle = listOf(zone) + getAllChildren(zone)

                // Si ya está seleccionado, quitamos padre e hijos; si no, agregamos todos
                if (currentList.any { it.id == zone.id }) {
                    currentList.removeAll { it.id in zonesToToggle.map { z -> z.id } }
                } else {
                    currentList.addAll(zonesToToggle.filter { it !in currentList })
                }

                _uiState.value = _uiState.value.copy(
                    selectionToRemoveZones = currentList.toSet()
                )
            }


            is ZonePrincipalUiEvent.ToggleDeleteDialogState -> {
                if (event.state) {
                    if (_uiState.value.filteredZones.isNotEmpty()) {
                        _uiState.value = _uiState.value.copy(
                            showDeleteZonesDialog = true
                        )
                    } else {
                        showToast("No hay zonas disponibles para eliminar")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        showDeleteZonesDialog = false
                    )
                }
            }


            ZonePrincipalUiEvent.ClearSelectedDeleteZoneList -> _uiState.value =
                _uiState.value.copy(selectionToRemoveZones = setOf())

            is ZonePrincipalUiEvent.OnAddFilter -> {
                updateState {
                    copy(
                        filters = filters
                            .filterNot { it.type == event.filter.type }
                            .plus(event.filter)
                    )
                }
                applyFilters()
            }

            is ZonePrincipalUiEvent.OnChangeQuery -> {
                updateState { copy(searchQuery = event.query) }
                applyFilters()
            }

            is ZonePrincipalUiEvent.OnRemoveFilter -> {
                updateState {
                    copy(
                        filters = filters.minus(
                            event.filter
                        )
                    )
                }
                applyFilters()
            }
        }
    }

    private fun deleteSelectedZones() {
        viewModelScope.launch {
            // Activar loading
            updateState { copy(isLoading = true) }

            try {
                val state = _uiState.value
                val userId = state.userId ?: run {
                    showToast("El id del usuario no es válido")
                    return@launch
                }

                removeZoneListCase(userId, state.selectionToRemoveZones.toList())
                    .onSuccess {
                        // Limpiar selección y actualizar lista de zonas
                        updateState {
                            copy(selectionMode = false, selectionToRemoveZones = setOf())
                        }
                        callAndUpdateUserZoneList()
                    }
                    .onError { error ->
                        showToast(error.message)
                    }
            } finally {
                // Desactivar loading siempre
                updateState { copy(isLoading = false) }
            }
        }
    }


    private fun callAndUpdateUserZoneList() {
        viewModelScope.launch {
            val userId = _uiState.value.userId ?: ""

            // Activar loading
            updateState { copy(isLoading = true) }

            try {
                // Suscribirse al Flow de zonas
                getAllZoneList(userId)
                    .catch { throwable ->
                        // Manejo de errores del Flow
                        showToast(throwable.message ?: "Error al obtener zonas")
                    }
                    .collect { allZoneList ->
                        // Actualiza la lista de zonas
                        updateState { copy(allZoneList = allZoneList) }
                        // Aplica filtros
                        applyFilters()
                    }
            } finally {
                // Desactivar loading al final
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(ZonePrincipalUiEffect.ShowToast(message))
        }
    }

    private fun applyFilters() {
        val state = _uiState.value

        val filtered = filterZones(
            zoneSearchQuery = state.searchQuery,
            filtersList = state.filters,
            allZonesList = state.allZoneList
        )

        _uiState.value = state.copy(
            filteredZones = filtered
        )
    }


}