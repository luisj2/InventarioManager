package com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.MainScreen

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.presentation.ui.navigation.PrincipalScreen


@Composable
fun MainScreen(
    screenContent: @Composable (screenSelected : PrincipalScreen) -> Unit,
    viewModel : MainScreenViewModel = viewModel()
) {

    val uiState by viewModel.uiState

    MainScreenContent(
        screenContent = screenContent,
        uiState = uiState,
        onEvent = {event -> viewModel.onEvent(event)}
    )
}

@Composable
private fun MainScreenContent(
    screenContent: @Composable (PrincipalScreen) -> Unit,
    uiState: MainScreenUiState,
    onEvent : (event : MainScreenUiEvent) -> Unit
) {
    val principalScreens = PrincipalScreen.entries

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                screens = principalScreens,
                screenSelected = uiState.screenSelected,
                onScreenSelected = {newScreen-> onEvent(MainScreenUiEvent.ChangeScreenClick(newScreen)) }
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues)){
            AnimatedContent(targetState = uiState.screenSelected, label = "MainBottomNavTransition") { targetScreen ->
                screenContent(targetScreen)
            }
        }

    }
}

@Composable
private fun BottomNavigationBar(
    screens: List<PrincipalScreen>,
    screenSelected: PrincipalScreen,
    onScreenSelected: (PrincipalScreen) -> Unit
) {
    NavigationBar {
        screens.forEach { screen ->
            val isScreenSelected = screen == screenSelected

            NavigationBarItem(
                selected = isScreenSelected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(text = screen.title) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}
