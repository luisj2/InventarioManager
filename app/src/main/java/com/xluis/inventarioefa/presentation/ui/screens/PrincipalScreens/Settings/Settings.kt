package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.toast

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navigateToLogin: () -> Unit,
    navigateBack : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit){
        viewModel.onEvent(SettingsUiEvent.InitSession)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                SettingUiEffect.NavigateBack -> navigateBack()
                SettingUiEffect.NavigateToLogin -> navigateToLogin()
                is SettingUiEffect.ShowToast -> context.toast(effect.message)
            }
        }
    }

    if(uiState.isLoading){
        LoadingIndicator()
    }

    SettingsContent(
        state = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onEvent: (event : SettingsUiEvent) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (state.isLoggedIn) {
            LogInContent(
                username = state.username ?: "???",
                onLogOut = {
                    onEvent(SettingsUiEvent.OnLogoutClicked)
                }
            )
        } else {
            LogOutContent (
                onLogin = { onEvent(SettingsUiEvent.OnLoginClicked) }
            )
        }
    }
}

@Composable
private fun LogInContent(
    username : String,
    onLogOut : () -> Unit
){
    Text(
        text = "Sesión iniciada como:",
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = username ?: "",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { onLogOut() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cerrar sesión")
    }
}

@Composable
private fun LogOutContent(
    onLogin : () -> Unit
){
    Text(
        text = "No has iniciado sesión",
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = { onLogin() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Iniciar sesión")
    }
}


