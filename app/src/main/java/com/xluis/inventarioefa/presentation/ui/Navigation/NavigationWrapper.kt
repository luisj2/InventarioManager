package com.xluis.inventarioefa.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.xluis.inventarioefa.di.AppDependencies
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.presentation.ViewModel.ArticleMovementsSelectionSharedViewModel
import com.xluis.inventarioefa.presentation.ViewModel.ArticleSelectionSharedViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Login.LoginViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Auth.MainAuthScreen
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Register.RegisterViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.MainScreen.MainScreen
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.MainScreen.MainScreenUiEvent
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.MainScreen.MainScreenViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings.SettingsScreen
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings.SettingsViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements.YourMovementsScreen
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements.YourMovementsViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal.ZonePrincipalScreen
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal.ZonePrincipalViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen.ZoneRequestsScreen
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen.ZoneRequestsViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector.ArticleListSelector
import com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector.ArticleListSelectorViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector.ZoneSelectorScreen
import com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector.ZoneSelectorViewModel
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.ZoneInfoScreen
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.ZoneInfoViewModel
import com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone.CreateZoneScreen
import com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone.CreateZoneViewModel

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    // Lambdas de navegación
    val navigateBack: () -> Unit = { navController.popBackStack() }
    val mainViewModel = getVM<MainScreenViewModel>()
    val navigateToSetting =
        { mainViewModel.onEvent(MainScreenUiEvent.ChangeScreenClick(PrincipalScreen.Settings)) }

    // Shared ViewModel para artículos seleccionados
    val articleSelectionSharedViewModel: ArticleSelectionSharedViewModel = viewModel()
    val selectedArticlesByScreen by articleSelectionSharedViewModel.selectedArticlesByScreen.collectAsState()
    val articleMovementsSelectionSharedViewModel: ArticleMovementsSelectionSharedViewModel =
        viewModel()
    val selectedMovementsByScreen by articleMovementsSelectionSharedViewModel.selectedMovementsByScreen.collectAsState()

    NavHost(navController = navController, startDestination = Screen.MainScreen) {

        // 🔹 Main Screen
        composable<Screen.MainScreen> {
            MainScreen(
                screenContent = { screenSelected ->
                    when (screenSelected) {
                        PrincipalScreen.Zones -> ZonePrincipalScreen(
                            viewModel = getVM<ZonePrincipalViewModel>(),
                            navigateToCreateZone = { parentZoneId, storageType ->
                                navController.navigate(Screen.CreateZone(parentZoneId, storageType))
                            },
                            navigateToZoneInfo = { zoneId, storageType ->
                                navController.navigate(Screen.ZoneInfo(zoneId, storageType))
                            },
                            navigateToSettings = navigateToSetting
                        )

                        PrincipalScreen.Settings -> SettingsScreen(
                            viewModel = getVM<SettingsViewModel>(),
                            navigateToLogin = { navController.navigate(Screen.MainAuth) },
                            navigateBack = navigateBack
                        )

                        PrincipalScreen.Movements -> YourMovementsScreen(viewModel = getVM<YourMovementsViewModel>())
                        PrincipalScreen.ZoneRequests -> ZoneRequestsScreen(viewModel = getVM<ZoneRequestsViewModel>())
                    }
                },
                viewModel = mainViewModel
            )
        }

        // 🔹 Auth
        composable<Screen.MainAuth> {
            MainAuthScreen(
                navigateMainScreen = { navController.navigate(Screen.MainScreen) },
                loginViewModel = getVM<LoginViewModel>(),
                registerViewModel = getVM<RegisterViewModel>()
            )
        }

        // 🔹 Zone principal
        composable<Screen.ZonePrincipal> {
            ZonePrincipalScreen(
                viewModel = getVM<ZonePrincipalViewModel>(),
                navigateToCreateZone = { parentZoneId, storageType ->
                    navController.navigate(Screen.CreateZone(parentZoneId, storageType))
                },
                navigateToZoneInfo = { zoneId, storageType ->
                    navController.navigate(Screen.ZoneInfo(zoneId, storageType))
                },
                navigateToSettings = navigateToSetting
            )
        }

        // 🔹 Create Zone
        composable<Screen.CreateZone> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.CreateZone>()
            val storageType = StorageType.fromName(args.storageType)
            CreateZoneScreen(
                viewModel = getVM<CreateZoneViewModel>(),
                parentZoneId = args.parentZoneId,
                storageType = storageType,
                navigateBack = navigateBack
            )
        }

        // 🔹 Zone Info
        composable<Screen.ZoneInfo> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ZoneInfo>()
            val zoneInfoViewModel = getVM<ZoneInfoViewModel>()
            val screen = "ZoneInfo"
            ZoneInfoScreen(
                zoneId = args.zoneId,
                storageType = args.storageType,
                navigateToArticleSelector = {
                    navController.navigate(
                        Screen.ArticleListSelector(
                            args.storageType,
                            args.zoneId,
                            screen
                        )
                    )
                },
                navigateZoneSelector = { zoneIdToMove, articleIdToMove, articleCountToMove,zoneFromMoveStorageType, zoneToMoveStorageType ->
                    navController.navigate(
                        Screen.ZoneSelector(
                            zoneIdToMove,
                            zoneFromMoveStorageType,
                            zoneToMoveStorageType,
                            articleIdToMove,
                            articleCountToMove
                        )
                    )
                },
                updateZone = { zoneId, _ -> zoneInfoViewModel.observeZone(zoneId) },
                viewModel = zoneInfoViewModel,
                screenId = screen,
                onNavigateBack = navigateBack
            )
        }

        // 🔹 Zone Selector
        composable<Screen.ZoneSelector> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ZoneSelector>()
            ZoneSelectorScreen(
                viewModel = getVM<ZoneSelectorViewModel>(),
                zoneIdFromMove = args.zoneIdFromMove,
                zoneToMoveStorageType = args.zoneSelectedStorageType,
                zoneFromMoveStorageType = args.zoneFromMoveStorageType,
                articleCountToMove = args.articleCountToMove,
                articleIdToMove = args.articleIdToMove,
                navigateBack = navigateBack
            )
        }

        // 🔹 Your Movements
        composable<Screen.YourMovements> {
            YourMovementsScreen(viewModel = getVM<YourMovementsViewModel>())
        }

        // 🔹 Article Selector
        composable<Screen.ArticleListSelector> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ArticleListSelector>()
            ArticleListSelector(
                viewModel = getVM<ArticleListSelectorViewModel>(),
                zoneId = args.zoneId ?: "",
                storageType = args.storageType,
                screenId = args.screenId,
                navigateBack = navigateBack
            )
        }

        // 🔹 Settings
        composable<Screen.Settings> {
            SettingsScreen(
                viewModel = getVM<SettingsViewModel>(),
                navigateToLogin = {
                    navController.navigate(Screen.MainAuth) {
                        popUpTo(Screen.MainScreen) { inclusive = true }
                    }
                },
                navigateBack = navigateBack
            )
        }

        // 🔹 Zone Requests
        composable<Screen.ZoneRequests> {
            ZoneRequestsScreen(viewModel = getVM<ZoneRequestsViewModel>())
        }
    }
}


@Composable
inline fun <reified VM : ViewModel> getVM(): VM {
    return viewModel(factory = AppDependencies.getFactory(VM::class))
}


@Composable
fun ScreenContentFor(
    screenSelected: PrincipalScreen,
    navController: NavController,
    navigateToSetting: () -> Unit,
    navigateBack: () -> Unit
) {
    when (screenSelected) {

        PrincipalScreen.Zones -> {
            ZonePrincipalScreen(
                viewModel = getVM<ZonePrincipalViewModel>(),
                navigateToCreateZone = { parentZoneId, storageType ->
                    navController.navigate(Screen.CreateZone(parentZoneId, storageType))
                },
                navigateToZoneInfo = { zoneId, storageType ->
                    navController.navigate(Screen.ZoneInfo(zoneId, storageType))
                },
                navigateToSettings = navigateToSetting
            )
        }

        PrincipalScreen.Settings -> {
            SettingsScreen(
                viewModel = getVM<SettingsViewModel>(),
                navigateToLogin = { navController.navigate(Screen.MainAuth) },
                navigateBack = navigateBack
            )
        }

        PrincipalScreen.Movements -> {
            YourMovementsScreen(
                viewModel = getVM<YourMovementsViewModel>()
            )
        }

        PrincipalScreen.ZoneRequests -> {
            ZoneRequestsScreen(
                viewModel = getVM<ZoneRequestsViewModel>()
            )
        }
    }
}




