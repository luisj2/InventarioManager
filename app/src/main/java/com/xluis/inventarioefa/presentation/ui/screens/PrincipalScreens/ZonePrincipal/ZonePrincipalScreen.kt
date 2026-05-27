package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.SearchAndFiltersBar
import com.xluis.inventarioefa.utils.ZoneListSimple
import com.xluis.inventarioefa.utils.toast


@Composable
fun ZonePrincipalScreen(
    viewModel: ZonePrincipalViewModel,
    navigateToCreateZone: (parentId: String?, storageType: String) -> Unit,
    navigateToZoneInfo: (zoneId: String, storageType: String) -> Unit,
    navigateToSettings : () -> Unit
) {

    val uiState by viewModel.uiState
    val context = LocalContext.current



    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ZonePrincipalUiEffect.NavigateToCreateZone -> {
                    navigateToCreateZone(effect.parentId, effect.storageType.name)
                }

                is ZonePrincipalUiEffect.NavigateToZoneInfo -> {
                    navigateToZoneInfo(effect.zoneId, effect.storageType)
                }

                is ZonePrincipalUiEffect.ShowToast -> context.toast(effect.message)
                ZonePrincipalUiEffect.NavigateToSettingScreen -> navigateToSettings()
            }
        }
    }


    if (uiState.isLoading) {
        LoadingIndicator()
    }

    ZonePrincipalContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )

    DeleteSelectedZonesDialog(
        showDialog = uiState.showDeleteZonesDialog,
        onDismiss = { viewModel.onEvent(ZonePrincipalUiEvent.ToggleDeleteDialogState(false)) },
        onConfirmDelete = { viewModel.onEvent(ZonePrincipalUiEvent.DeleteSelectedZones) },
        onDesactivateDeleteMode = {viewModel.onEvent(ZonePrincipalUiEvent.ToggleDeleteDialogState(false))},
        selectedCount = uiState.selectionToRemoveZones.size
    )


}

@Composable
fun DeleteSelectedZonesDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDesactivateDeleteMode : () -> Unit,
    selectedCount: Int
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Eliminar zonas") },
            text = {
                Text(
                    "¿Deseas eliminar ${if (selectedCount == 1) "la zona seleccionada" else "las $selectedCount zonas seleccionadas"}?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirmDelete()
                    onDesactivateDeleteMode()
                    onDismiss()
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
private fun ZonePrincipalContent(
    uiState: ZonePrincipalUiState,
    onEvent: (ZonePrincipalUiEvent) -> Unit
) {

    Scaffold(
        topBar = {
            ZoneTopBar(
                selectionModeState = uiState.selectionMode,
                selectedCount = uiState.selectionToRemoveZones.size,
                onToggleOption = {
                    if (uiState.selectionMode) onEvent(ZonePrincipalUiEvent.DesactivateSelectZoneToRemoveMode)
                    else onEvent(ZonePrincipalUiEvent.ActivateSelectZoneToRemoveMode)
                },
                onDeleteSelectedZones = { onEvent(ZonePrincipalUiEvent.ToggleDeleteDialogState(true)) }
            )
        },
        floatingActionButton = {
            FancyFabMenu(
                uiState = uiState,
                onEvent = onEvent,
                onAddForMeZone = {
                    onEvent(ZonePrincipalUiEvent.NavigateToCreateLocalZone(null))
                },
                onAddSharedZone = {
                    onEvent(
                        ZonePrincipalUiEvent.NavigateToCreateFirebaseZone(
                            null
                        )
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SearchAndFiltersBar(
                query = uiState.searchQuery,
                filters = uiState.filters,
                onQueryChanged = { onEvent(ZonePrincipalUiEvent.OnChangeQuery(it)) },
                onFilterAdded = { onEvent(ZonePrincipalUiEvent.OnAddFilter(it)) },
                onFilterRemoved = { onEvent(ZonePrincipalUiEvent.OnRemoveFilter(it)) }
            )

            ZoneListSimple(
                zones = zonesToShow(uiState),
                allZonesList = uiState.filteredZones,
                selectedZones = uiState.selectionToRemoveZones.toList(),

                onZoneClick = { zone ->
                    if (uiState.selectionMode) {
                        onEvent(ZonePrincipalUiEvent.ToggleZoneDeleteSelection(zone))
                    } else {
                        zone.id?.let {
                            onEvent(
                                ZonePrincipalUiEvent.NavigateToZoneInfo(
                                    it,
                                    zone.storageType.name
                                )
                            )
                        }
                    }
                },
                onZoneLongClick = { zone ->
                    onEvent(ZonePrincipalUiEvent.ActivateSelectZoneToRemoveMode)
                    onEvent(ZonePrincipalUiEvent.ToggleZoneDeleteSelection(zone))
                },

                onAddSubzoneClick = { parentId, storageType ->
                    when (storageType) {
                        StorageType.LOCAL ->
                            onEvent(ZonePrincipalUiEvent.NavigateToCreateLocalZone(parentId))

                        StorageType.FIREBASE ->
                            onEvent(ZonePrincipalUiEvent.NavigateToCreateFirebaseZone(parentId))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZoneTopBar(
    selectionModeState: Boolean,
    selectedCount: Int,
    onToggleOption: () -> Unit,
    onDeleteSelectedZones: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            if (selectionModeState) {
                IconButton(onClick = onToggleOption) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salir de selección"
                    )
                }
            }
        },
        title = {
            Text(
                text = if (selectionModeState)
                    "$selectedCount seleccionados"
                else
                    "Zonas",
                style = MaterialTheme.typography.titleMedium
            )
        },
        actions = {
            if (!selectionModeState) {
                IconButton(onClick = onToggleOption) {
                    Icon(
                        imageVector = Icons.Default.CheckBox,
                        contentDescription = "Activar selección",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (selectionModeState && selectedCount > 0) {
                IconButton(onClick = onDeleteSelectedZones) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        tint = Color.Red,
                        contentDescription = "Eliminar seleccionados"
                    )
                }
            }
        }
    )
}


@Composable
fun FancyFabMenu(
    uiState: ZonePrincipalUiState,
    onEvent: (event: ZonePrincipalUiEvent) -> Unit,
    onAddForMeZone: () -> Unit,
    onAddSharedZone: () -> Unit
) {

    val isMenuOpen = uiState.isCreateZoneMenuOpen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { onEvent(ZonePrincipalUiEvent.CloseCreateZoneMenu) }
            )
        }

        // Card con opciones
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .width(220.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Crear Zona",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = {
                            onAddForMeZone()
                            onEvent(ZonePrincipalUiEvent.CloseCreateZoneMenu)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Para mí", color = Color.White)
                    }
                    Button(
                        onClick = {
                            onEvent(ZonePrincipalUiEvent.CloseCreateZoneMenu)
                            onAddSharedZone()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Compartida", color = Color.White)
                    }
                }
            }
        }

        // FAB principal
        FloatingActionButton(
            onClick = {
                if (isMenuOpen) onEvent(ZonePrincipalUiEvent.CloseCreateZoneMenu)
                else onEvent(ZonePrincipalUiEvent.OpenCreateZoneMenu)
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (isMenuOpen) Icons.Default.Close else Icons.Default.Add,
                contentDescription = "Más opciones",
                tint = Color.White            )
        }
    }
}

private fun zonesToShow(
    uiState : ZonePrincipalUiState
) : List<Zone>{
    return if (uiState.searchQuery.isBlank()) {
        // 🔹 Sin búsqueda → solo padres
        uiState.filteredZones.filter { it.parentIdList.isNullOrEmpty() }
    } else {
        // 🔹 Con búsqueda → todas las zonas que coincidan
        uiState.filteredZones
    }
}




