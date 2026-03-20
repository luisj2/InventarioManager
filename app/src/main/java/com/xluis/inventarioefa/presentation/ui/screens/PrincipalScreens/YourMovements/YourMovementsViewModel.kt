package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.GetAllUserMovements
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.Enums.DateMode
import com.xluis.inventarioefa._domain.model.Enums.SortType
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class YourMovementsViewModel(
    private val getAllUserMovements : GetAllUserMovements
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
                val action = event.action
                val filtered = applyFilters(movementList, searchQuery, action)
                copy(
                    selectedAction = action,
                    filteredMovements = applySort(filtered, sortType)
                )
            }

            is YourMovementsUiEvents.OnSearchChanged -> updateState {
                val query = event.query
                val filtered = applyFilters(movementList, query, selectedAction)
                copy(
                    searchQuery = query,
                    filteredMovements = applySort(filtered, sortType)
                )
            }



            is YourMovementsUiEvents.OnSortOrderChanged -> updateState {
                copy(
                    sortType = event.sortType,
                    filteredMovements = applySort(filteredMovements, event.sortType)
                )
            }

            YourMovementsUiEvents.OnToggleDate -> updateState {
                val newDateMode = DateMode.toggle(dateMode)
                val filtered = applyFilters(movementList, searchQuery, selectedAction)
                val sorted = sortByDate(filtered, newDateMode)

                copy(
                    dateMode = newDateMode,
                    filteredMovements = sorted
                )
            }
        }


    }

    private fun sortByDate(list: List<ArticleMovement>, dateMode: DateMode): List<ArticleMovement> {
        return when (dateMode) {
            DateMode.ASCENDING -> list.sortedBy { it.date }
            DateMode.DESCENDING -> list.sortedByDescending { it.date }
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

    private fun applyFilters(
        list: List<ArticleMovement>,
        query: String,
        action: MovementAction?
    ): List<ArticleMovement> {
        return list
            .filter { action == null || it.actionType == action }
            .filter {
                query.isBlank() || it.articleName.contains(query, ignoreCase = true) || it.zoneName.contains(query, ignoreCase = true)
            }
    }


    private fun updateMovementsByUserId() {
        val uid = _uiState.value.userId ?: run{
            showToast("No se ha encontrado el id del usuario")
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            getAllUserMovements(uid)
                .onSuccess { movements -> updateState { copy(movementList = movements, filteredMovements = movements) } }
                .onError { error -> showToast(error.message) }

            updateState { copy(isLoading = false) }
        }
    }



    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(YourMovementUiEffect.ShowToast(message))
        }
    }


}