package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.MainScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainScreenViewModel : ViewModel() {

    private val _uiState = mutableStateOf(MainScreenUiState())
    val uiState: State<MainScreenUiState> = _uiState

    fun onEvent(event : MainScreenUiEvent){
        when(event){
            is MainScreenUiEvent.ChangeScreenClick -> _uiState.value = uiState.value.copy(screenSelected = event.screen)
        }
    }

}