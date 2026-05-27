package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.utils.CurvedBorderBackground
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.DefaultTextField
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.toast

@Composable
fun CreateZoneScreen(
    viewModel: CreateZoneViewModel,
    parentZoneId: String?,
    storageType: StorageType,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onEvent(CreateZoneUiEvent.InitValues(parentZoneId, storageType))
    }

    LaunchedEffect(uiState.userId) {
        if (uiState.userId != null) {
            viewModel.onEvent(CreateZoneUiEvent.InitParentListByUserId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                CreateZoneUiEffect.NavigateBack -> navigateBack()
                is CreateZoneUiEffect.ShowToast -> context.toast(effect.message)
            }
        }
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    }

    CurvedBorderBackground {
        SingleFormContent(
            uiState = uiState,
            onEvent = { event -> viewModel.onEvent(event) },
            showToast = { message -> viewModel.onEvent(CreateZoneUiEvent.ShowToast(message)) }
        )
    }
}

@Composable
private fun SingleFormContent(
    uiState: CreateZoneUiState,
    onEvent: (event: CreateZoneUiEvent) -> Unit,
    showToast: (message: String) -> Unit
) {

    Scaffold(
        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    onEvent(CreateZoneUiEvent.CreateZoneClicked(emptyList()))
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Crear zona",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔙 BACK BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clickable {
                            onEvent(CreateZoneUiEvent.NavigateBack)
                        },
                    tint = Color.White
                )
            }

            // 🔹 TITLE
            Text(
                "Crea tu Zona",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                "Crea tu propia zona y asegúrate que el desorden no gane esta vez",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
            )

            // 🔹 ZONE NAME
            DefaultTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.zoneName,
                onTextChange = {
                    onEvent(CreateZoneUiEvent.ZoneNameChanged(it))
                },
                label = "Nombre de la zona",
                rounded = true
            )

            // 🔹 PARENT LABEL
            Text(
                text = "Zona padre",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 2.dp)
            )

            // 🔹 DROPDOWN
            DefaultDropDownSelector(
                modifier = Modifier.fillMaxWidth(),
                optionList = uiState.parentList.map { it.name },
                labelText = "Zona padre",
                selectedOption = uiState.parentZoneId ?: uiState.noParentSelectedText,
                onOptionSelected = { selectedName ->
                    onEvent(
                        CreateZoneUiEvent.ZoneParentIdChanged(selectedName)
                    )
                }
            )
        }
    }
}