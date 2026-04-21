package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.xluis.inventarioefa.utils.DefaultButton
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 🔹 CONTENIDO SCROLLEABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 80.dp), // 🔥 espacio para el botón
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // 🔽 TODO TU CONTENIDO IGUAL (NO TOCAR)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clickable { onEvent(CreateZoneUiEvent.NavigateBack) },
                    tint = Color.White
                )
            }

            Text(
                "Crea tu Zona",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text(
                "Crea tu propia zona y asegúrate que el desorden no gane esta vez",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ... TODO lo demás igual ...
        }

        // 🔥 BOTÓN FIJO ABAJO
        DefaultButton(
            onClick = {
                    onEvent(CreateZoneUiEvent.CreateZoneClicked(emptyList()))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            contentText = "Crear Zona"
        )
    }
}