package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.Enums.DateMode
import com.xluis.inventarioefa._domain.model.Enums.SortType
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction

data class YourMovementsUiState (
    val isLoading : Boolean = false,
    val userId : String? = null,
    val movementList : List<ArticleMovement> = emptyList(),
    val filteredMovements: List<ArticleMovement> = emptyList(),
    val searchQuery : String = "",
    val sortType: SortType? = null,
    val selectedAction: MovementAction? = null,
    val dateMode : DateMode = DateMode.DESCENDING
    )