package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.presentation.ui.theme.GreenPrimary
import com.xluis.inventarioefa.utils.ArticleItem
import com.xluis.inventarioefa.utils.CurvedBorderBackground
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.INVENTORY_ICON_MAP
import com.xluis.inventarioefa.utils.IconPickerDialog
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.ValidatedTextField
import com.xluis.inventarioefa.utils.toast


@Composable
fun CreateZoneScreen(
    viewModel: CreateZoneViewModel,
    parentZoneId : String?,
    storageType : StorageType,
    navigateArticleSelector: () -> Unit,
    navigateBack: () -> Unit
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onEvent(CreateZoneUiEvent.InitValues(parentZoneId,storageType))
        viewModel.onEvent(CreateZoneUiEvent.InitParentListByUserId)
    }

    LaunchedEffect(Unit) {

        viewModel.uiEffect.collect { effect ->
            when (effect) {
                CreateZoneUiEffect.NavigateBack -> navigateBack()
                CreateZoneUiEffect.NavigateToZoneSelector -> navigateArticleSelector()
                is CreateZoneUiEffect.ShowToast -> context.toast(effect.message)
            }
        }


    }

    if (uiState.isLoading) {
        LoadingIndicator()
    }

    CurvedBorderBackground {
        CreateZoneContent(
            uiState = uiState,
            onEvent = { event -> viewModel.onEvent(event) },
            showToast = { message -> viewModel.onEvent(CreateZoneUiEvent.ShowToast(message)) }
        )
    }
}

@Composable
fun AddArticleDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,

        title = { Text("Nuevo artículo") },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                ValidatedTextField(
                    value = name,
                    onTextChange = { name = it },
                    isError = name.isBlank(),
                    errorMessage = if (name.isBlank()) "Campo requerido" else null,
                    label = "Nombre del artículo"
                )

                ValidatedTextField(
                    value = category,
                    onTextChange = { category = it },
                    isError = category.isBlank(),
                    errorMessage = if (category.isBlank()) "Campo requerido" else null,
                    label = "Categoría"
                )
            }
        },

        confirmButton = {
            DefaultButton(
                onClick = {
                    if (name.isNotBlank() && category.isNotBlank()) {
                        onSave(name, category)
                        onDismiss()
                    }
                },
                contentText = "Guardar"
            )
        },

        dismissButton = {
            Text(
                text = "Cancelar",
                modifier = Modifier.clickable(onClick = onDismiss),
                color = Color.Gray
            )
        }
    )
}


@Composable
private fun CreateZoneContent(
    uiState: CreateZoneUiState,
    onEvent: (event: CreateZoneUiEvent) -> Unit,
    showToast: (message : String) -> Unit
) {

    val pageMap = mapOf(
        1 to CreateZonePages.ZONE_FORM,
        2 to CreateZonePages.ADD_ARTICLES
    )
    when (pageMap[uiState.page]) {
        CreateZonePages.ZONE_FORM -> FormContent(
            uiState = uiState,
            onEvent = onEvent,
            showToast = showToast
        )

        CreateZonePages.ADD_ARTICLES -> AddArticlesContent(
            uiState = uiState,
            onEvent = onEvent,
        )

        else -> showToast("Ha ocurrido un problema con el cambio de pantalla")
    }
}


@Composable
private fun FormContent(
    uiState: CreateZoneUiState,
    onEvent: (event: CreateZoneUiEvent) -> Unit,
    showToast : (message : String) -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ---------------------------
        // Barra superior con volver y siguiente
        // ---------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Botón de volver (izquierda)
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .clickable { onEvent(CreateZoneUiEvent.NavigateBack) },
                tint = Color.White
            )

            // Texto "Siguiente" (derecha)
            Text(
                text = "Siguiente",
                color = Color.Blue,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable {
                        if (uiState.zoneName.isBlank()) showToast("Rellena el campo del nombre de la zona")
                        else onEvent(CreateZoneUiEvent.NavigateNextPage)
                    }
            )
        }

        // ---------------------------
        // Título y subtítulo
        // ---------------------------
        Text("Crea tu Zona", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Text(
            "Crea tu propia zona y asegúrate que el desorden no gane esta vez",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------------------
        // Icono + Nombre
        // ---------------------------
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(0.17f)
                    .clickable { onEvent(CreateZoneUiEvent.OpenIconSelector) }
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    uiState.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            ValidatedTextField(
                modifier = Modifier.weight(1f),
                value = uiState.zoneName,
                onTextChange = { newZoneName ->
                    onEvent(CreateZoneUiEvent.ZoneNameChanged(newZoneName))
                },
                isError = uiState.zoneNameError != null,
                errorMessage = uiState.zoneNameError,
                label = "Nombre de la Zona"
            )
        }

        // ---------------------------
        // Diálogo de selección de icono
        // ---------------------------
        if (uiState.isIconSelectorOpen) {
            IconPickerDialog(
                icons = INVENTORY_ICON_MAP.values.toList(),
                onIconSelected = { newIcon ->
                    onEvent(CreateZoneUiEvent.IconChanged(newIcon))
                },
                onDismiss = { onEvent(CreateZoneUiEvent.CloseIconSelector) }
            )
        }

        // ---------------------------
        // Selector de zona padre
        // ---------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                val parentOptions = uiState.parentList.map { it.name } + uiState.noParentSelectedText
                val selectedParent = uiState.parentZoneId?.takeIf { it.isNotEmpty() } ?: uiState.noParentSelectedText

                if (!uiState.isParentPredefined) {
                    Text(
                        text = "Elige dentro de qué zona está, sino selecciona 'Ninguna'",
                        color = Color.Black
                    )
                    DefaultDropDownSelector(
                        optionList = parentOptions,
                        selectedOption = selectedParent,
                        labelText = "Zona padre (opcional)",
                        onOptionSelected = { newParentId ->
                            onEvent(CreateZoneUiEvent.ZoneParentIdChanged(newParentId))
                        }
                    )
                } else {
                    Text(
                        text = "Zona padre: ${uiState.parentName}",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun AddArticlesContent(
    uiState: CreateZoneUiState,
    onEvent: (CreateZoneUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // 🔹 Header superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Anterior",
                color = Color.Blue,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.clickable {
                    onEvent(CreateZoneUiEvent.NavigatePreviousPage)
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { onEvent(CreateZoneUiEvent.NavigateToZoneSelector) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir artículo",
                    tint = Color.Black
                )
            }
        }

        // 🔹 Lista de artículos scrollable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (uiState.articleSelectedList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.articleSelectedList) { article ->
                        ArticleItem(article = article)
                    }
                }
            } else {
                // Mensaje cuando no hay artículos
                Text(
                    text = "No hay artículos añadidos",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Botón guardar zona siempre visible al final
        DefaultButton(
            onClick = { onEvent(CreateZoneUiEvent.CreateZoneClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            contentText = "Guardar Zona"
        )


    }
}















