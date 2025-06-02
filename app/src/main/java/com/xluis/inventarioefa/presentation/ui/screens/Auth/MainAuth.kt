package com.xluis.inventarioefa.presentation.ui.screens.Auth

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultTopBar
import kotlinx.coroutines.launch

@Composable
fun MainAuthScreen(
    navigateToInventaryData: () -> Unit
) {

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelBuilder.getAuthViewModelFactory()
    )
    if (authViewModel.isLoggedIn) navigateToInventaryData()

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { PagerAuthItems.entries.size })
    val selectedTAbIdex = remember { derivedStateOf { pagerState.currentPage } }
    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "Autenticación",
                haveBackButton = false
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = selectedTAbIdex.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                PagerAuthItems.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTAbIdex.value == index,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.outline,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.ordinal)
                            }
                        },
                        text = { Text(text = currentTab.text) },
                        icon = {
                            Icon(
                                imageVector =
                                if (selectedTAbIdex.value == index) currentTab.selectedIcon
                                else currentTab.unselectedIcon,
                                contentDescription = "Tab Icon"
                            )
                        }
                    )
                }

            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val currentTabPager = PagerAuthItems.entries[selectedTAbIdex.value]
                    PagerAuthContent(currentTabPager,navigateToInventaryData)
                }
            }
        }
    }
}

@Composable
private fun PagerAuthContent(
    item: PagerAuthItems,
    navigateToInventaryData: () -> Unit
) {
    when (item) {
        PagerAuthItems.Login -> LoginScreen(navigateToInventaryData)
        PagerAuthItems.Register -> RegisterScreen()
        else -> Text(text = item.text)
    }
}

