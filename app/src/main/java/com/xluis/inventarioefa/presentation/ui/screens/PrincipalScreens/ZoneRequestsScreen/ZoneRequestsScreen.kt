package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa._domain.model.User.ZoneRequest
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.toast
import toReadableDateTime


@Composable
fun ZoneRequestsScreen(
    viewModel: ZoneRequestsViewModel
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(Unit){
        viewModel.onEvent(ZoneRequestsUiEvent.ChargeUserZoneRequests)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ZoneRequestsUiEffect.showToast -> context.toast(effect.message)
            }
        }
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    }
    ZoneRequestsContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )

}

@Composable
fun ZoneRequestsContent(
    uiState: ZoneRequestsUiState,
    onEvent: (event: ZoneRequestsUiEvent) -> Unit
) {
    if (uiState.requestsList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No hay solicitudes pendientes")
        }
        return
    }

    if(uiState.isLoading){
        LoadingIndicator()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(uiState.requestsList) { request ->
            ZoneRequestItem(
                request = request,
                onAccept = { zoneRequest ->
                    onEvent(
                        ZoneRequestsUiEvent.AcceptZoneRequest(
                            zoneRequest
                        )
                    )
                },
                onReject = { requestId -> onEvent(ZoneRequestsUiEvent.RejectZoneRequest(requestId)) }
            )
        }
    }
}

@Composable
fun ZoneRequestItem(
    request: ZoneRequest,
    onAccept: (zoneRequest: ZoneRequest) -> Unit,
    onReject: (requestId: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Usuario: ${request.requesterName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Zona: ${request.zoneName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Creada: ${request.createdAt.toReadableDateTime()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onReject(request.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Rechazar", color = MaterialTheme.colorScheme.onError)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onAccept(request) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Aceptar", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }


        }
    }
}
