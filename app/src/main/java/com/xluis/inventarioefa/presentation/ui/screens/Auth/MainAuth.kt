package com.xluis.inventarioefa.presentation.ui.screens.Auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Login.LoginScreen
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Login.LoginViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Register.RegisterScreen
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Register.RegisterViewModel
import com.xluis.inventarioefa.utils.DefaultTopBar
import kotlinx.coroutines.launch

@Composable
fun MainAuthScreen(
    navigateMainScreen : () -> Unit,
    loginViewModel : LoginViewModel,
    registerViewModel: RegisterViewModel
) {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { PagerAuthItems.entries.size })

    Scaffold(
        topBar = {
            DefaultTopBar(title = "Autenticación", haveBackButton = false)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            // 🔹 TabRow sincronizada
            TabRow(selectedTabIndex = pagerState.currentPage) {
                PagerAuthItems.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(text = currentTab.text) },
                        icon = {
                            Icon(
                                imageVector =
                                if (pagerState.currentPage == index) currentTab.selectedIcon
                                else currentTab.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // 🔹 Pager que conserva estado y no recomposea completo
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { page ->

                when (PagerAuthItems.entries[page]) {
                    PagerAuthItems.Login -> LoginScreen(
                        navigateMainScreen,
                        loginViewModel
                    )

                    PagerAuthItems.Register -> RegisterScreen(
                        registerViewModel,
                        onNavigateToLogin = {email,password->
                            loginViewModel.setCredentials(email,password)
                            scope.launch {
                                pagerState.animateScrollToPage(PagerAuthItems.Login.ordinal)
                            }
                        }
                    )
                }
            }
        }
    }
}




