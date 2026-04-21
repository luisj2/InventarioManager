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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa._domain.model.Enums.DateMode
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.MovementsList



@Composable
fun YourMovementsScreen(
    viewModel: YourMovementsViewModel = viewModel()
) {

    val uiState by viewModel.uiState



    LaunchedEffect(Unit) {
        viewModel.onEvent(YourMovementsUiEvents.UpdateMovementListByUserId)
    }

    if(uiState.isLoading){
        LoadingIndicator()
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
            dateMode = uiState.dateMode,
            onSearch = { onEvent(YourMovementsUiEvents.OnSearchChanged(it)) },
            onActionSelected = { onEvent(YourMovementsUiEvents.OnActionFilterChanged(it)) },
            onToggleDate = { onEvent(YourMovementsUiEvents.OnToggleDate)}
        )

        // 🔹 Lista o mensaje vacío
        if (uiState.filteredMovements.isEmpty()) {
            Text(
                text = "No has hecho ningún movimiento",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            )
        } else {
            MovementsList(
                modifier = Modifier.fillMaxWidth(),
                movementsList = uiState.filteredMovements
            )
        }
    }
}


@Composable
fun MovementFilterBar(
    searchQuery: String,
    selectedAction: MovementAction?,
    dateMode : DateMode,
    onSearch: (String) -> Unit,
    onActionSelected: (MovementAction?) -> Unit,
    onToggleDate: () -> Unit
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
                    else onActionSelected(MovementAction.fromName(option))
                },
                modifier = Modifier.weight(1f)
            )

            // 📅 ORDEN FECHA
            DefaultButton(
                contentText = dateMode.displayName,
                onClick = {  onToggleDate() },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
