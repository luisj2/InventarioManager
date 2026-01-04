package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector

import ArticleCategory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.Enums.SortType
import com.xluis.inventarioefa.utils.ArticleItem
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.toast


@Composable
fun ArticleListSelector(
    viewModel: ArticleListSelectorViewModel,
    storageType : String,
    zoneId : String,
    onConfirmSelection : (selectedArticles :List<Article>,selectedMovements: List<ArticleMovement>) -> Unit,
    navigateBack: () -> Unit
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.onEvent(ArticleListSelectorUiEvent.InitValues(storageType,zoneId))
        viewModel.onEvent(ArticleListSelectorUiEvent.GetAllArticles)
    }

    LaunchedEffect(Unit){
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ArticleListSelectorUiEffect.NavigateBack -> navigateBack()
                is ArticleListSelectorUiEffect.ArticlesSaved -> {
                    onConfirmSelection(effect.selectedArticles,effect.selectedMovements)
                }

                is ArticleListSelectorUiEffect.ShowToast -> context.toast(effect.message)
            }
        }

    }

    if(uiState.isLoading){
        LoadingIndicator()
    }

    ArticleSelectorContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) },
        showToast = { message -> viewModel.showToast(message) }
    )

}

@Composable
private fun ArticleSelectorContent(
    uiState: ArticleListSelectorUiState,
    onEvent: (event: ArticleListSelectorUiEvent) -> Unit,
    showToast: (message: String) -> Unit
) {

    val articleList = uiState.articleList
    val selectedArticles = uiState.selectedArticleList

    Scaffold(
        topBar = {
            ArticleListSelectorTopBar(
                onBackClick = {
                    onEvent(ArticleListSelectorUiEvent.NavigateBack)
                }
            )
        }
    ) {paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                DefaultButton(
                    contentText = "+ Crear artículo",
                    onClick = {
                        onEvent(ArticleListSelectorUiEvent.ToggleCreateArticleDialog(true))
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                DefaultButton(
                    contentText = "+ Añadir ${selectedArticles.size}",
                    onClick = {
                        if (articleList.isNotEmpty()) {
                            onEvent(ArticleListSelectorUiEvent.SaveSelectedArticles)
                            onEvent(ArticleListSelectorUiEvent.NavigateBack)
                        } else {
                            showToast("Añade un artículo")
                        }
                    }
                )
            }

            ArticleFilterBar(
                state = uiState,
                onSearch = { query -> onEvent(ArticleListSelectorUiEvent.OnSearchQueryChanged(query)) },
                onCategorySelected = { category -> onEvent(ArticleListSelectorUiEvent.OnCategoryChanged(category)) },
                onSortSelected = { sort -> onEvent(ArticleListSelectorUiEvent.OnAddSortType(sort)) },
                onSortRemoved = {sort -> onEvent(ArticleListSelectorUiEvent.OnRemoveSortType(sort))}
            )

            // Lista de artículos
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.filteredArticleList) { article ->
                    val isArticleSelected = uiState.selectedArticleList.any { it.id == article.id }
                    ArticleItem(
                        article = article,
                        isArticleSelected = isArticleSelected,
                        onSelectArticle = { count ->
                            if (!isArticleSelected) {
                                onEvent(ArticleListSelectorUiEvent.SelectArticleList(article, count))
                            } else {
                                onEvent(ArticleListSelectorUiEvent.DeselectArticleByIdList(article.id))
                            }
                        }
                    )
                }
            }


            // Diálogo para crear un artículo
            AddArticleDialog(
                showDialog = uiState.createArticleDialogState,
                onDismiss = { onEvent(ArticleListSelectorUiEvent.ToggleCreateArticleDialog(false)) },
                onAddArticle = { article -> onEvent(ArticleListSelectorUiEvent.AddArticle(article)) }
            )
        }
    }
}

@Composable
fun ArticleFilterBar(
    state: ArticleListSelectorUiState,
    onSearch: (String) -> Unit,
    onCategorySelected: (ArticleCategory?) -> Unit,
    onSortSelected: (SortType) -> Unit,
    onSortRemoved: (SortType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // FILTROS PRINCIPALES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearch,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar") },
                singleLine = true
            )

            DefaultDropDownSelector(
                optionList = listOf("Todas") + ArticleCategory.entries.map { it.displayName },
                labelText = "Categoría",
                selectedOption = state.selectedCategory?.displayName ?: "Todas",
                onOptionSelected = { selected ->
                    if (selected == "Todas") onCategorySelected(null)
                    else {
                        val category = ArticleCategory.entries.first { it.displayName == selected }
                        onCategorySelected(category)
                    }
                },
            )

            SortButton(
                onSortSelected = onSortSelected
            )
        }


        if (state.sortList.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.sortList.forEach { sort ->
                    SortChip(sort = sort, onRemove = { onSortRemoved(sort) })
                }
            }
        }
    }
}


@Composable
fun SortButton(
    onSortSelected: (SortType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Ordenar",
                tint = Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortType.entries.forEach { sort ->
                DropdownMenuItem(
                    text = { Text(sort.displayName) },
                    onClick = {
                        expanded = false
                        onSortSelected(sort)
                    }
                )
            }
        }
    }
}
@Composable
fun SortChip(sort: SortType, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(4.dp)
            .padding(horizontal = 8.dp)
            .padding(vertical = 4.dp)
            .fillMaxWidth(0.5f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = sort.displayName,
            color = Color.Black
        )
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar filtro"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListSelectorTopBar(
    title: String = "Seleccionar artículos",
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title, color = MaterialTheme.colorScheme.onPrimary)
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}





