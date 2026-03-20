 package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.SimpleZoneSelector
import com.xluis.inventarioefa.utils.toast


@Composable
fun ZoneSelectorScreen(
    viewModel: ZoneSelectorViewModel = viewModel(),
    zoneIdFromMove: String,
    zoneFromMoveStorageType : String,
    zoneToMoveStorageType: String,
    articleIdToMove: String,
    articleCountToMove: Int,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            ZoneSelectorUiEvent.Initialize(
                zoneIdFromMove = zoneIdFromMove,
                zoneToMoveStorageType = StorageType.fromName(zoneToMoveStorageType),
                storageTypeFromMove = StorageType.fromName(zoneFromMoveStorageType),
                articleIdToMove = articleIdToMove,
                articleCountToMove = articleCountToMove
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(ZoneSelectorUiEvent.GetArticleById(zoneIdFromMove, articleIdToMove))
    }

    LaunchedEffect(
        uiState.userId,
        uiState.zoneIdFromMove,
        uiState.storageTypeFromMove
    ) {
        val userId = uiState.userId
        val zoneId = uiState.zoneIdFromMove
        val storageType = uiState.storageTypeFromMove

        if (userId != null && zoneId != null && storageType != null) {
            viewModel.onEvent(ZoneSelectorUiEvent.GetZoneListByUserId)
        }
    }


    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ZoneSelectorUiEffect.NavigateBack -> navigateBack()
                is ZoneSelectorUiEffect.ShowToast -> context.toast(effect.message)
            }
        }
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    }

    ZoneSelectorContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )

}


@Composable
private fun ZoneSelectorContent(
    uiState: ZoneSelectorUiState,
    onEvent: (event: ZoneSelectorUiEvent) -> Unit
) {
    Scaffold(
        topBar = {
            ZoneSelectorTopBar(
                onBackClick = { onEvent(ZoneSelectorUiEvent.NavigateBack) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Selecciona la Zona",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DefaultButton(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    contentText = "Si",
                    textColor = Color.Black,
                    containerColor = if (uiState.zoneIdSelected != null) Color.Green else Color.Gray,
                    onClick = {
                        if (uiState.zoneIdSelected == null) onEvent(ZoneSelectorUiEvent.ShowToast("Selecciona alguna zona"))
                        else {
                            onEvent(ZoneSelectorUiEvent.SaveMoveArticleMove)
                        }
                    }
                )

                DefaultButton(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    contentText = "No",
                    textColor = Color.Black,
                    containerColor = Color.Red,
                    onClick = { onEvent(ZoneSelectorUiEvent.NavigateBack) }
                )

            }

            SimpleZoneSelector(
                zones = uiState.zoneList.filter { it.id != uiState.zoneIdFromMove },
                onZoneClick = { zone ->
                    zone.id?.let { id->
                        if(uiState.zoneIdSelected == id) onEvent(ZoneSelectorUiEvent.DeselectZone)
                        else onEvent(ZoneSelectorUiEvent.SelectZone(zoneIdSelected = id, storageType = zone.storageType))
                    }
                },
                selectedZoneIds = listOfNotNull(uiState.zoneIdSelected)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectorTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Selecciona la Zona",
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}





