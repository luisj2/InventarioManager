@file:OptIn(ExperimentalMaterial3Api::class)

package com.xluis.inventarioefa.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.R
import com.xluis.inventarioefa.domain.model.DataClass.Article.Article
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleFilters.ArticleFilters
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleFilters.SortOptions
import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.Category
import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.State
import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.Ubication
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Result.UiEvent
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication.UbicationFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication.UbicationFirestoreViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultAdviseMessageDialog
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.handleErrors
import com.xluis.inventarioefa.utils.toast
import java.util.TreeMap

@Composable
fun InventoryDataScreen(
    navigateToArticleInfo: (String) -> Unit,
    navigateToCreateArticle: () -> Unit,
    navigateToMovementRegister: () -> Unit,
    navigateToEditArticle: (String) -> Unit,
    returnToAuth: () -> Unit
) {

    //ViewModels
    val ubicationViewModel: UbicationFirestoreViewModel =
        viewModel(
            factory = UbicationFirestoreViewModelBuilder.getUbicationFirestoreViewModelFactory()
        )
    val authViewModel: AuthViewModel =
        viewModel(
            factory = AuthViewModelBuilder.getAuthViewModelFactory()
        )

    val articleViewModel: ArticleFirestoreViewModel = viewModel(
        factory = ArticleFirestoreViewModelBuilder.getArticleViewModelFactory()
    )

    var articleSelectedId by remember { mutableStateOf("") }

    //Lists
    val articleListResult by articleViewModel.allArticlesList.collectAsState()
    val ubicationListResult by ubicationViewModel.allUbicationsList.collectAsState()
    val ubicationRegisterStatus by ubicationViewModel.insertUbicationStatus.collectAsState()


    val originalArticleList = if (articleListResult is SuspendResult.Success) {
        (articleListResult as SuspendResult.Success).data
    } else {
        emptyList()
    }

    val ubicationList = if (ubicationListResult is SuspendResult.Success) {
        (ubicationListResult as SuspendResult.Success).data
    } else {
        emptyList()
    }

    val context = LocalContext.current

    LaunchedEffect(articleListResult, ubicationListResult) {

        arrayOf(
            articleListResult,
            ubicationListResult
        ).handleErrors { message->
            context.toast(message)
        }
    }

    LaunchedEffect(ubicationRegisterStatus){
        if(ubicationRegisterStatus is SuspendResult.Success) context.toast("Ubicacion registada correctamente")
    }


    //Loading
    val isLoading = (articleListResult is SuspendResult.Loading)
            || (ubicationListResult is SuspendResult.Loading)






    LaunchedEffect(Unit) {
        articleViewModel.getAllArticles()
        ubicationViewModel.getAllUbications()

    }


    //Dialogs
    var showCreateUbicationDialog by remember { mutableStateOf(false) }
    var showRemoveArticleDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }


    ManageDialogs(
        showCreateUbicationDialog = showCreateUbicationDialog,
        onDismissCreateUbicationDialog = { showCreateUbicationDialog = false },
        onUbicationCreated = { ubicationName ->

            ubicationViewModel.insertUbicationInFirestore(
                Ubication(name = ubicationName)
            )

        },

        showLogoutDialog = showLogoutDialog,
        onDismissLogoutDialog = { showLogoutDialog = false },
        onLogoutClick = {
            authViewModel.logOut()
            returnToAuth()
        },

        showRemoveArticleDialog = showRemoveArticleDialog,
        onDismissRemoveArticleDialog = { showRemoveArticleDialog = false },
        onRemoveArticleDialog = {
            articleViewModel.removeArticleById(articleSelectedId)
            articleViewModel.getAllArticles()
        })

    val fabActionsMap = initFABActions(
        navigateToCreateArticle,
        showUbicationDialog = { showCreateUbicationDialog = true },
        navigateToMovementRegister = navigateToMovementRegister
    )



    ScreenViewContent(
        originalArticleList = originalArticleList,
        ubicationList = ubicationList,
        onLogoutClick = { showLogoutDialog = true },
        fabActionsMap = fabActionsMap,
        navigateToArticleInfo = navigateToArticleInfo,
        onRefreshArticleList = { articleViewModel.getAllArticles() },
        isRefreshingArticleList = articleListResult is SuspendResult.Loading,
        onEditArticle = { articleId -> navigateToEditArticle(articleId) },
        onRemoveArticle = { articleId ->
            articleSelectedId = articleId
            showRemoveArticleDialog = true
        }
    )

    if (isLoading) DefaultLoadingScreen()

}


private fun handleUiEvents(
    event: UiEvent,
    showMessage: (String) -> Unit
) {

    when (event) {
        is UiEvent.ShowToast -> showMessage(event.message)
        else -> {}
    }
}


@Composable
private fun ScreenViewContent(
    originalArticleList: List<Article>,
    ubicationList: List<Ubication>,
    onLogoutClick: () -> Unit,
    fabActionsMap: TreeMap<String, () -> Unit>,
    navigateToArticleInfo: (String) -> Unit,
    onRefreshArticleList: () -> Unit,
    isRefreshingArticleList: Boolean,
    onEditArticle: (String) -> Unit,
    onRemoveArticle: (String) -> Unit
) {

    var query by remember { mutableStateOf("") }
    var filterList by remember { mutableStateOf(originalArticleList) }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        val (inventorySearchBar, filterOptions, articlesListElements, addElementsFAB) = createRefs()

        Row(
            modifier = Modifier
                .constrainAs(inventorySearchBar) {
                    top.linkTo(parent.top, margin = 8.dp)
                    start.linkTo(parent.start, margin = 6.dp)
                    end.linkTo(parent.end, margin = 6.dp)
                }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LogoutButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onLogoutClick = onLogoutClick
            )
            Spacer(modifier = Modifier.width(8.dp))

            InventarySearchBar(
                modifier = Modifier.weight(1f),
                onSearchName = { searchValue ->
                    query = searchValue
                }
            )
        }


        FilterOptionsRow(
            modifier = Modifier
                .constrainAs(filterOptions) {
                    top.linkTo(inventorySearchBar.bottom, margin = 8.dp)
                    start.linkTo(parent.start, margin = 24.dp)
                },
            list = originalArticleList,
            onListUpdate = { filterList = it },
            ubicationList = ubicationList.map { it.name },
            query = query
        )

        if (filterList.isEmpty()) {
            EmptyListMessage()
        } else
            ArticleElementsLazyColumn(
                modifier = Modifier
                    .constrainAs(articlesListElements) {
                        top.linkTo(filterOptions.bottom, margin = 18.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(vertical = 60.dp),
                articleList = filterList,
                onRefresh = onRefreshArticleList,
                navigateToArticleInfo = navigateToArticleInfo,
                onEditArticle = onEditArticle,
                isRefreshing = isRefreshingArticleList,
                onRemoveArticle = onRemoveArticle
            )

        FloattingActionButton(
            Modifier.constrainAs(addElementsFAB) {
                end.linkTo(parent.end, margin = 20.dp)
                bottom.linkTo(parent.bottom, margin = 20.dp)
            },
            fabActionMap = fabActionsMap
        )
    }
}

@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = modifier.padding(start = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFFE53935),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ManageDialogs(
    showCreateUbicationDialog: Boolean,
    onDismissCreateUbicationDialog: () -> Unit,
    onUbicationCreated: (String) -> Unit,

    showLogoutDialog: Boolean,
    onDismissLogoutDialog: () -> Unit,
    onLogoutClick: () -> Unit,

    showRemoveArticleDialog: Boolean,
    onDismissRemoveArticleDialog: () -> Unit,
    onRemoveArticleDialog: () -> Unit
) {

    CreateUbicationDialog(
        showDialog = showCreateUbicationDialog,
        onDismiss = onDismissCreateUbicationDialog,
        onUbicationCreated = { ubicationName -> onUbicationCreated(ubicationName) }
    )

    DefaultAdviseMessageDialog(
        title = "Cerrar Sesión",
        message = "¿Estas seguro de Cerrar Sesión?",
        confirmButtonMessage = "Cerrar Sesión",
        confirmColor = Color.Red,
        showDialog = showLogoutDialog,
        onConfirm = onLogoutClick,
        onDismiss = onDismissLogoutDialog
    )

    DefaultAdviseMessageDialog(
        title = "Eliminar Artículo",
        message = "¿Seguro que quieres eliminar el articulo?",
        confirmButtonMessage = "Si,Eliminar",
        confirmColor = Color.Red,
        showDialog = showRemoveArticleDialog,
        onConfirm = onRemoveArticleDialog,
        onDismiss = onDismissRemoveArticleDialog
    )
}


@Composable
private fun FilterOptionsRow(
    modifier: Modifier,
    list: List<Article>,
    onListUpdate: (List<Article>) -> Unit,
    query: String,
    ubicationList: List<String>
) {

    val categorySelected = remember { mutableStateOf<String?>(null) }
    val ubicationSelected = remember { mutableStateOf<String?>(null) }
    val stateSelected = remember { mutableStateOf<String?>(null) }
    val orderSelected = remember { mutableStateOf<SortOptions?>(null) }

    val filteredList = remember { mutableStateOf<List<Article>>(listOf()) }

    val articleFilters = ArticleFilters(
        category = categorySelected.value,
        ubication = ubicationSelected.value,
        state = stateSelected.value,
        sortBy = orderSelected.value
    )

    filteredList.value = filterListBy(articleFilters, list).filter {
        it.name.contains(
            query.trim(),
            ignoreCase = true
        )
    }

    onListUpdate(filteredList.value)

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {

        Text(text = "Filtros:")

        FilterDropdown(
            label = "Ordenar Por",
            selectedOption = orderSelected.value?.displayName,
            options = SortOptions.entries.map { it.displayName },
            onOptionSelected = { selected ->
                orderSelected.value =
                    SortOptions.entries.first { it.displayName == selected }
            }
        )

        FilterDropdown(
            label = "Categoría",
            selectedOption = categorySelected.value,
            options = Category.entries.map { it.displayName }.plus("Todos"),
            onOptionSelected = { categorySelected.value = it }
        )

        FilterDropdown(
            label = "Ubicación",
            selectedOption = ubicationSelected.value,
            options = ubicationList.plus("Todos"),
            onOptionSelected = { ubicationSelected.value = it }
        )

        FilterDropdown(
            label = "Estado",
            selectedOption = stateSelected.value,
            options = State.entries.map { it.displayName }.plus("Todos"),
            onOptionSelected = { stateSelected.value = it }
        )

    }
}


private fun filterListBy(articleFilters: ArticleFilters, list: List<Article>): List<Article> {
    return list
        .asSequence()
        .filter { article ->
            articleFilters.category?.let {
                it == "Todos" || article.category == it
            } ?: true
        }
        .filter { article ->
            articleFilters.ubication?.let {
                it == "Todos" || article.ubication == it
            } ?: true
        }
        .filter { article ->
            articleFilters.state?.let {
                it == "Todos" || article.state == it
            } ?: true
        }
        .toList()
        .let { filtered ->
            when (articleFilters.sortBy) {
                SortOptions.CountAsc -> filtered.sortedBy { it.count }
                SortOptions.CountDesc -> filtered.sortedByDescending { it.count }
                SortOptions.NameAsc -> filtered.sortedBy { it.name }
                SortOptions.NameDesc -> filtered.sortedByDescending { it.name }
                else -> filtered
            }
        }
}


fun initFABActions(
    navigateToCreateArticle: () -> Unit,
    showUbicationDialog: () -> Unit,
    navigateToMovementRegister: () -> Unit
): TreeMap<String, () -> Unit> {
    return TreeMap(
        mapOf(
            "Crear Artículo" to navigateToCreateArticle,
            "Crear Ubicación" to showUbicationDialog,
            "Movimientos" to navigateToMovementRegister
        )
    )
}

@Composable
private fun ArticlesListElements(
    modifier: Modifier = Modifier,
    articlesList: List<Article>,
    navigateToArticleInfo: (String) -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onEditArticle: (String) -> Unit,
    onRemoveArticle: (String) -> Unit
) {
    ArticleElementsLazyColumn(
        modifier = modifier.padding(vertical = 60.dp),
        articleList = articlesList,
        onRefresh = onRefresh,
        navigateToArticleInfo = navigateToArticleInfo,
        onEditArticle = onEditArticle,
        isRefreshing = isRefreshing,
        onRemoveArticle = onRemoveArticle
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArticleElementsLazyColumn(
    modifier: Modifier = Modifier,
    articleList: List<Article>,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    navigateToArticleInfo: (String) -> Unit,
    onEditArticle: (String) -> Unit,
    onRemoveArticle: (String) -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()


    PullToRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articleList) { article ->
                ArticleItem(
                    article = article,
                    navigateToArticleInfo = navigateToArticleInfo,
                    onEditArticle,
                    onRemoveArticle
                )
            }
        }
    }
}


@Composable
private fun ArticleItem(
    article: Article,
    navigateToArticleInfo: (String) -> Unit,
    onEditArticle: (String) -> Unit,
    onRemoveArticle: (String) -> Unit
) {
    val image = Category.fromDisplayName(article.category ?: "")?.icon
    val expandedMenu = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable {
                navigateToArticleInfo(article.id ?: "")
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                painter = painterResource(id = image ?: R.drawable.category_other),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = article.name,
                    style = MaterialTheme.typography.titleMedium
                )
                article.ubication?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box {
                IconButton(onClick = { expandedMenu.value = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más opciones"
                    )
                }

                MenuItemOptions(
                    expanded = expandedMenu.value,
                    onDismiss = { expandedMenu.value = false },
                    options = listOf(
                        "Editar" to { onEditArticle(article.id ?: "") },
                        "Eliminar" to { onRemoveArticle(article.id ?: "") }
                    )
                )
            }
        }
    }
}


@Composable
fun MenuItemOptions(
    expanded: Boolean,
    onDismiss: () -> Unit,
    options: List<Pair<String, () -> Unit>>
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        options.forEach { (label, action) ->
            DropdownMenuItem(
                text = { Text(label) },
                onClick = {
                    action()
                    onDismiss()
                }
            )
        }
    }
}


@Composable
private fun EmptyListMessage(
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No hay ningun articulo...")
    }
}


@Composable
private fun InventarySearchBar(
    modifier: Modifier = Modifier,
    onSearchName: (String) -> Unit
) {
    val searchQuery = remember { mutableStateOf("") }

    Box(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { newValue ->
                searchQuery.value = newValue
                onSearchName(newValue)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            placeholder = {
                Text(
                    text = "Buscar artículo...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}



@Composable
private fun FloattingActionButton(
    modifier: Modifier = Modifier,
    fabActionMap: TreeMap<String, () -> Unit>
) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (expanded) ShowSmallFABs(menuActions = fabActionMap)

        FloatingActionButton(
            contentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.primary,
            onClick = { expanded = !expanded }) {
            if (expanded) Icon(imageVector = Icons.Filled.Close, contentDescription = null)
            else Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }


}

@Composable
private fun ShowSmallFABs(
    menuActions: TreeMap<String, () -> Unit>
) {
    menuActions.forEach { (functionalityName, screenTravelLogic) ->

        SmallFloatingActionButton(
            onClick = screenTravelLogic,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary
        ) {
            Text(text = functionalityName)
        }
    }

}

@Composable
private fun FilterDropdown(
    label: String,
    selectedOption: String?,
    options: List<String>,
    onOptionSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Row {
                Text(text = selectedOption ?: label)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CreateUbicationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onUbicationCreated: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Nueva ubicación") },
            text = {
                Column {
                    Text("Introduce el nombre de la ubicación:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nombre") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onUbicationCreated(name.trim())
                            onDismiss()
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}



