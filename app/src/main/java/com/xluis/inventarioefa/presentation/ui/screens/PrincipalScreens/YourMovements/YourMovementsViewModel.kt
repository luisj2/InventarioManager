package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetUserZonesIds
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.GetMovementsByZoneIdList
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements.GetAllMovementsUserZones
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.Enums.SortType
import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class YourMovementsViewModel(
    private val getUserZonesIds: GetUserZonesIds,
    private val getMovementsByZoneIdList: GetMovementsByZoneIdList,
    private val getAllRoomMovements: GetAllMovementsUserZones
) : ViewModel() {

    private val _uiState = mutableStateOf(YourMovementsUiState())
    val uiState: State<YourMovementsUiState> = _uiState

    private val _uiEffect = Channel<YourMovementUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

    private fun updateState(update: YourMovementsUiState.() -> YourMovementsUiState) {
        _uiState.value = _uiState.value.update()
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
            }
        }
    }

    fun onEvent(event: YourMovementsUiEvents) {
        when (event) {
            is YourMovementsUiEvents.UpdateMovementListByUserId -> updateMovementsByUserId()

            is YourMovementsUiEvents.OnActionFilterChanged -> updateState {
                val actionFilter = event.action
                val filteredList = movementList
                    .filter { movement -> actionFilter == null || movement.actionType == actionFilter }
                    .filter { movement ->
                        searchQuery.isEmpty() || movement.articleName.lowercase().contains(searchQuery) ||
                                movement.zoneName.lowercase().contains(searchQuery)
                    }
                copy(
                    selectedAction = actionFilter,
                    filteredMovements = applySort(filteredList, sortType)
                )
            }

            is YourMovementsUiEvents.OnSearchChanged -> updateState {
                val query = event.query.lowercase()
                val filteredList = movementList
                    .filter { movement ->
                        selectedAction == null || movement.actionType == selectedAction
                    }
                    .filter { movement ->
                        query.isEmpty() || movement.articleName.lowercase().contains(query) ||
                                movement.zoneName.lowercase().contains(query)
                    }
                copy(
                    searchQuery = query,
                    filteredMovements = applySort(filteredList, sortType)
                )
            }

            is YourMovementsUiEvents.OnSortOrderChanged -> updateState {
                copy(
                    sortType = event.sortType,
                    filteredMovements = applySort(filteredMovements, event.sortType)
                )
            }
        }
    }

    private fun applySort(
        list: List<ArticleMovement>,
        sort: SortType?
    ): List<ArticleMovement> {
        return when (sort) {
            SortType.BY_NAME_ASC -> list.sortedBy { it.articleName.lowercase() }
            SortType.BY_NAME_DESC -> list.sortedByDescending { it.articleName.lowercase() }
            SortType.BY_COUNT_ASC -> list.sortedBy { it.count }
            SortType.BY_COUNT_DESC -> list.sortedByDescending { it.count }
            null -> list
        }
    }

    private fun updateMovementsByUserId() {
        val uid = _uiState.value.userId ?: return
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getUserZonesIds(uid)
                .flatMap { zonesIdList ->
                    if (zonesIdList.isEmpty()) {
                        SuspendResult.Success(emptyList())
                    } else {
                        getMovementsByZoneIdList(zonesIdList)
                    }
                }
                .onSuccess { movementList ->
                    if (movementList.isNotEmpty()) updateState { copy(movementList = movementList) }
                }
                .onError { message ->
                    showToast(message.message)
                }

            getAllRoomMovements()
                .onSuccess { movementsList ->
                    updateState { copy(movementList = movementsList) }
                }
                .onError { error ->
                    showToast(error.message)
                }

            updateState { copy(isLoading = false) }
        }
    }


    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(YourMovementUiEffect.ShowToast(message))
        }
    }


}