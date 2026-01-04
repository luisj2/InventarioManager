package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements

import com.xluis.inventarioefa._domain.model.Enums.SortType
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction

sealed class YourMovementsUiEvents {
    data object UpdateMovementListByUserId: YourMovementsUiEvents()

    data class OnSearchChanged(val query: String) : YourMovementsUiEvents()
    data class OnActionFilterChanged(val action: MovementAction?) : YourMovementsUiEvents()
    data class OnSortOrderChanged(val sortType: SortType) : YourMovementsUiEvents()
}