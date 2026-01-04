package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.MovementsList



@Composable
fun YourMovementsScreen(
    viewModel: YourMovementsViewModel = viewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState



    LaunchedEffect(Unit) {
        viewModel.onEvent(YourMovementsUiEvents.UpdateMovementListByUserId)
    }

    YourMovementContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )
}


@Composable
private fun YourMovementContent(
    uiState: YourMovementsUiState,
    onEvent: (YourMovementsUiEvents) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {

        MovementFilterBar(
            searchQuery = uiState.searchQuery,
            selectedAction = uiState.selectedAction,
            isDescending = uiState.sortDescending,
            onSearch = { onEvent(YourMovementsUiEvents.OnSearchChanged(it)) },
            onActionSelected = { onEvent(YourMovementsUiEvents.OnActionFilterChanged(it)) },
            onDateSortChanged = { }
        )

        // Lista filtrada
        MovementsList(
            modifier = Modifier.fillMaxWidth(),
            movementsList = uiState.filteredMovements
        )
    }
}


@Composable
fun MovementFilterBar(
    searchQuery: String,
    selectedAction: MovementAction?,
    isDescending: Boolean,
    onSearch: (String) -> Unit,
    onActionSelected: (MovementAction?) -> Unit,
    onDateSortChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // 🔍 BUSCADOR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearch,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por artículo…") },
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            // 🔽 FILTRO TIPO DE ACCIÓN
            DefaultDropDownSelector(
                optionList = listOf("Todos") + MovementAction.entries.map { it.displayName },
                labelText = "Tipo",
                selectedOption = selectedAction?.name ?: "Todos",
                onOptionSelected = { option ->
                    if (option == "Todos") onActionSelected(null)
                    else onActionSelected(MovementAction.valueOf(option))
                },
                modifier = Modifier.weight(1f)
            )

            // 📅 ORDEN FECHA
            DefaultButton(
                contentText = if (isDescending) "Fecha ↓" else "Fecha ↑",
                onClick = { onDateSortChanged(!isDescending) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
