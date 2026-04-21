package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.Zone.ZoneMember
import com.xluis.inventarioefa._domain.util.parseArticlesFromSVG
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs.AddMemberDialog
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs.AddRemoveQuantityDialog
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs.ChangeZoneNameDialog
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs.ConfirmDeleteDialog
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs.ConfirmDeleteMemberDialog
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs.ConfirmExitDialog
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs.MoveQuantityDialog
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.PageZoneItems.BaseZonePages
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.PageZoneItems.ShareZonePages
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.LoadingIndicator
import com.xluis.inventarioefa.utils.MovementsList
import com.xluis.inventarioefa.utils.toast
import kotlinx.coroutines.launch

@Composable
fun ZoneInfoScreen(
    zoneId: String,
    storageType: String,
    viewModel: ZoneInfoViewModel,
    navigateToArticleSelector: () -> Unit,
    updateZone: (zoneId: String, storageType: String) -> Unit,
    navigateZoneSelector: (zoneIdFromMove: String, articleIdToMove: String, articleCountToMove: Int, zoneFromMoveStorageType: String, zoneToMoveStorageType: String) -> Unit,
    screenId: String,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState
    val context = LocalContext.current

    val backLogic: () -> Unit = rememberBackLogic(
        viewModel.uiState,
        onEvent = { event -> viewModel.onEvent(event) },
        onNavigateBack
    )

    Dialogs(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) }
    )

    LaunchedEffect(Unit) {
        viewModel.onEvent(ZoneInfoUiEvent.InitValues(storageType, screenId))
        if (viewModel.uiState.value.zone == null) {
            viewModel.onEvent(ZoneInfoUiEvent.UpdateZone(zoneId))
        }
    }

    LaunchedEffect(uiState.zone,uiState.screenId) {
        if(uiState.screenId == null || uiState.zone?.id == null) return@LaunchedEffect
        viewModel.onEvent(ZoneInfoUiEvent.InitToSaveListsFlows)
    }


    var openSvgPicker by remember { mutableStateOf(false) }

    SvgPickerHandler(
        openPicker = openSvgPicker,
        zoneId = uiState.zone?.id ?: "",
        onArticlesParsed = { articles -> viewModel.onEvent(ZoneInfoUiEvent.ImportArticles(articles)) },
        onShowToast = { message -> viewModel.showToast(message) },
        onOpenHandled = { openSvgPicker = false }
    )

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ZoneInfoUiEffect.NavigateBack -> {
                    onNavigateBack()
                }

                ZoneInfoUiEffect.NavigateToArticleSelector -> navigateToArticleSelector()
                is ZoneInfoUiEffect.NavigateToZone -> updateZone(effect.zoneId, storageType)
                ZoneInfoUiEffect.NavigateToZoneSelector -> {
                    val article = uiState.articleToMoveSelected
                    val articleId = article?.id
                    val articleCount = article?.count
                    val storageTypeSelector = uiState.zone?.storageType?.name
                    val storageTypeFromMove = uiState.storageType.name
                    if (articleId == null || articleCount == null || storageTypeSelector == null) {
                        viewModel.showToast("No se puede mover ese artículo")
                        return@collect
                    }
                    navigateZoneSelector(
                        zoneId,
                        articleId,
                        articleCount,
                        storageTypeFromMove,
                        storageTypeSelector
                    )
                }

                is ZoneInfoUiEffect.ShowToast -> context.toast(effect.message)
                ZoneInfoUiEffect.OpenSVGSelector -> openSvgPicker = true
                ZoneInfoUiEffect.NavigateBackShowDialog -> {
                    backLogic()
                }
                else -> {}
            }
        }
    }

    if(uiState.isLoading){
        LoadingIndicator()
    }

    ZoneInfoContent(
        uiState = uiState,
        onEvent = { event -> viewModel.onEvent(event) },
    )
}

@Composable
private fun Dialogs(
    uiState: ZoneInfoUiState,
    onEvent: (ZoneInfoUiEvent) -> Unit
) {
    ConfirmExitDialog(
        show = uiState.showConfirmLeftDialog,
        onConfirm = {
            onEvent(ZoneInfoUiEvent.RemoveArticlesToSave)
            onEvent(ZoneInfoUiEvent.DissmissConfirmLeftDialog)
            onEvent(ZoneInfoUiEvent.NavigateBack)
        },
        onDismiss = {
            onEvent(ZoneInfoUiEvent.DissmissConfirmLeftDialog)
        }
    )
    MoveQuantityDialog(
        show = uiState.showMoveDialog,
        maxCount = uiState.articleToMoveSelected?.count ?: 0,
        dismissDialog = {
            onEvent(ZoneInfoUiEvent.ToggleShowMoveDialog(false))
        },
        onMove = { countToMove ->
            onEvent(ZoneInfoUiEvent.SetMoveCount(countToMove))
            onEvent(ZoneInfoUiEvent.MoveSelectedArticle)
        }
    )

    AddMemberDialog(
        show = uiState.shareDialogState,
        onDismiss = { onEvent(ZoneInfoUiEvent.ToggleShareDialogState(false)) },
        onSend = { email ->
            onEvent(ZoneInfoUiEvent.AddMemeberByEmail(email))
            onEvent(ZoneInfoUiEvent.ToggleShareDialogState(false))
        }
    )

    ConfirmDeleteDialog(
        show = uiState.showDeleteArticlesDialog,
        articleCount = uiState.selectionArticleToRemove.size,
        onConfirm = {
            onEvent(ZoneInfoUiEvent.RemoveArticlesById)
        },
        onDismiss = { onEvent(ZoneInfoUiEvent.DismissDeleteArticleDialog) })

    AddRemoveQuantityDialog(
        show = uiState.showQuantityDialog,
        maxCount = uiState.articleToModifyCountSelected?.count ?: 0,
        onDismiss = { onEvent(ZoneInfoUiEvent.DissmissQuantityDialog) },
        onConfirm = {isAdd, quantity ->
            onEvent(ZoneInfoUiEvent.AddRemoveQuantity(isAdd,quantity))
            onEvent(ZoneInfoUiEvent.DissmissQuantityDialog)
        }
    )

    ConfirmDeleteMemberDialog(
        show = uiState.showDeleteMemberDialog,
        memberName = uiState.zoneMemberToRemove?.name ?: "este miembro",
        onConfirm = { onEvent(ZoneInfoUiEvent.RemoveMember) },
        onDismiss = { onEvent(ZoneInfoUiEvent.DissmissRemoveMemberDialog) }
    )

    ChangeZoneNameDialog(
        show = uiState.showChangeZoneNameDialog,
        onDismiss = { onEvent(ZoneInfoUiEvent.DismissZoneNameDialog) },
        onSaveClick = {newZoneName -> onEvent(ZoneInfoUiEvent.ChangeZoneName(newZoneName))}
    )
}


@Composable
fun rememberBackLogic(
    uiStateState: State<ZoneInfoUiState>,
    onEvent: (ZoneInfoUiEvent) -> Unit,
    onNavigateBack: () -> Unit
): () -> Unit {
    val uiState by uiStateState

    val backAction: () -> Unit = {
        if (uiState.articlesToSaveList.isNotEmpty() || uiState.movementsToSaveList.isNotEmpty()) {
            onEvent(ZoneInfoUiEvent.ShowConfirmLeftDialog)
        } else {
            onNavigateBack()
        }
    }

    BackHandler { backAction() }

    return backAction
}


@Composable
private fun ZoneInfoContent(
    uiState: ZoneInfoUiState,
    onEvent: (ZoneInfoUiEvent) -> Unit,
) {

    //Pages
    val pages = remember(uiState.storageType) {
        if (uiState.storageType == StorageType.FIREBASE) {
            ShareZonePages.pages()
        } else {
            BaseZonePages.pages()
        }
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })


    Scaffold(
        topBar = {
            if (uiState.selectionRemoveModeState) {
                ZoneInfoSelectionTopBar(
                    selectedCount = uiState.selectionArticleToRemove.size,
                    onExitSelection = {
                        onEvent(ZoneInfoUiEvent.ToggleSelectionDeleteArticleMode(false))
                        onEvent(ZoneInfoUiEvent.ClearArticleDeleteSelections)
                    },
                    onDeleteSelected = { onEvent(ZoneInfoUiEvent.ShowDeleteArticleDialog) }
                )
            } else {
                ZoneInfoTopBar(
                    uiState = uiState,
                    isSharedZone = uiState.zone?.ownerId == uiState.userId && uiState.storageType == StorageType.FIREBASE,
                    isInArticlePage = pages[pagerState.currentPage] is BaseZonePages.Articles,
                    onShareZone = { onEvent(ZoneInfoUiEvent.ToggleShareDialogState(true)) },
                    onSaveZone = {
                        onEvent(ZoneInfoUiEvent.SaveChanges)
                    },
                    onToggleSelectionMode = {
                        onEvent(ZoneInfoUiEvent.ToggleSelectionDeleteArticleMode(true))
                    },
                    onEditZoneName = {onEvent(ZoneInfoUiEvent.ShowZoneNameDialog)},
                    onBack = {
                        onEvent(ZoneInfoUiEvent.NavigateBackShowDialog)
                    }
                )
            }
        },
        floatingActionButton = {
            val currentPage = pages[pagerState.currentPage]
            if (currentPage is BaseZonePages.Articles) {
                FloatingActionButton(
                    onClick = { onEvent(ZoneInfoUiEvent.NavigateToArticleSelector) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) { Icon(Icons.Default.Add, contentDescription = "Seleccionar artículo") }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            ZoneBreadcrumbs(
                uiState = uiState,
                onNavigateToZone = { id -> onEvent(ZoneInfoUiEvent.NavigateToZone(id)) })

            HorizontalZonePagerWithState(
                pagerState = pagerState,
                pagerItems = pages
            ) { page ->
                Column(modifier = Modifier.weight(1f)) {
                    when (page) {
                        is BaseZonePages.Articles -> ArticleEditableList(
                            uiState = uiState,
                            selectedArticlesIdList = uiState.selectionArticleToRemove,
                            selectionRemoveModeState = uiState.selectionRemoveModeState,
                            onActivateActionMode = { article ->
                                onEvent(ZoneInfoUiEvent.ToggleSelectionDeleteArticleMode(true))
                                onEvent(ZoneInfoUiEvent.SelectArticle(article.id))
                            },
                            onMoveArticle = { article ->
                                onEvent(ZoneInfoUiEvent.SetMoveArticle(article))
                                if(article.count == 1) onEvent(ZoneInfoUiEvent.MoveSelectedArticle)
                                else onEvent(ZoneInfoUiEvent.ToggleShowMoveDialog(true))
                            },
                            onSelectArticle = { articleId ->
                                onEvent(ZoneInfoUiEvent.SelectArticle(articleId))
                            },
                            onDeselectArticle = { articleId ->
                                onEvent(ZoneInfoUiEvent.DeselectArticle(articleId))
                            },
                            onModifyCountClick = { articleToModify ->
                                onEvent(ZoneInfoUiEvent.UpdateArticleToModifyQuantity(articleToModify))
                                onEvent(ZoneInfoUiEvent.ShowQuantityDialog)
                            }
                        )


                        is BaseZonePages.Movements -> MovementsContent(movementsList = uiState.movementList)
                        is ShareZonePages.Members -> MemberListContent(
                            isOwner = uiState.userId == uiState.zone?.ownerId,
                            memberList = uiState.memberList,
                            owner = uiState.ownerMember,
                            onDeleteMember = { member -> onEvent(ZoneInfoUiEvent.ShowRemoveMemberDialog(member)) }
                        )
                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
fun ZoneInfoSelectionTopBar(
    selectedCount: Int,
    onExitSelection: () -> Unit,
    onDeleteSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // Botón para salir
        IconButton(onClick = onExitSelection) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Salir de selección",
                tint = Color.Black
            )
        }

        Text(
            text = "$selectedCount seleccionados",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        // Botón para borrar
        IconButton(onClick = onDeleteSelected) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar seleccionados",
                tint = Color.Red
            )
        }
    }
}


@Composable
private fun MemberListContent(
    owner: ZoneMember?,
    memberList: List<ZoneMember>,
    isOwner: Boolean,
    onDeleteMember: (member : ZoneMember) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Mostrar propietario primero
        owner?.let {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    MemberItem(
                        member = it,
                        isOwner = true,
                        showDelete = false,
                        onDelete = { onDeleteMember(it) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // Encabezado "Miembros"
        if (memberList.isNotEmpty()) {
            item {
                Text(
                    text = "Miembros",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }

        // Lista de miembros
        items(memberList) { member ->
            MemberItem(
                member = member,
                isOwner = false,
                showDelete = isOwner,
                onDelete = { onDeleteMember(member) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun MemberItem(
    member: ZoneMember,
    isOwner: Boolean,
    showDelete: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = member.name,
                    fontSize = if (isOwner) 20.sp else 16.sp,
                    fontWeight = if (isOwner) FontWeight.Bold else FontWeight.Normal,
                    color = if (isOwner) Color(0xFF81D4FA) else Color.White // Más claro para fondo negro
                )
                if (isOwner) {
                    Text(
                        text = "Propietario",
                        fontSize = 12.sp,
                        color = Color.LightGray // Se ve bien sobre negro
                    )
                }
            }

            if (showDelete) {
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar miembro",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun ArticleEditableList(
    uiState: ZoneInfoUiState,
    selectedArticlesIdList: List<String>,
    selectionRemoveModeState: Boolean,
    onActivateActionMode: (article: Article) -> Unit,
    onMoveArticle: (article: Article) -> Unit,
    onSelectArticle: (articleId: String) -> Unit,
    onDeselectArticle: (articleId: String) -> Unit,
    onModifyCountClick: (articleToModify: Article) -> Unit
) {
    val normalArticles = uiState.articleList
    val articlesToSave = uiState.articlesToSaveList

    if (normalArticles.isEmpty() && articlesToSave.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay artículos disponibles",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        // ✅ Mostrar lista de artículos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(normalArticles) { article ->
                val isSelected = selectedArticlesIdList.contains(article.id)

                ArticleEditableItem(
                    article = article,
                    isSelected = isSelected,
                    selectionRemoveModeState = selectionRemoveModeState,
                    canMove = true,
                    onSelectArticle = {
                        if (!isSelected) onSelectArticle(article.id)
                        else onDeselectArticle(article.id)
                    },
                    onActivateActionMode = onActivateActionMode,
                    onMoveArticle = onMoveArticle,
                    onModifyCountClick = { onModifyCountClick(article) }
                )
            }

            if (articlesToSave.isNotEmpty()) {
                item {
                    Text(
                        text = "Artículos para guardar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(articlesToSave) { article ->
                    val isSelected = selectedArticlesIdList.contains(article.id)

                    ArticleEditableItem(
                        article = article,
                        isSelected = isSelected,
                        selectionRemoveModeState = selectionRemoveModeState,
                        canMove = false,
                        onSelectArticle = {
                            if (!isSelected) onSelectArticle(article.id)
                            else onDeselectArticle(article.id)
                        },
                        onActivateActionMode = onActivateActionMode,
                        onMoveArticle = {},
                        onModifyCountClick = { onModifyCountClick(article) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArticleEditableItem(
    article: Article,
    isSelected: Boolean,
    selectionRemoveModeState: Boolean,
    canMove: Boolean,
    onSelectArticle: () -> Unit,
    onActivateActionMode: (article: Article) -> Unit,
    onMoveArticle: (article: Article) -> Unit,
    onModifyCountClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .combinedClickable(
                onClick = { if (selectionRemoveModeState) onSelectArticle() },
                onLongClick = { onActivateActionMode(article) }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = article.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black // Texto principal en negro
                )

                Text(
                    text = "Categoría: ${article.category.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black // Color negro en vez de gris
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Cantidad:",
                        color = Color.Black // Negro para etiqueta
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = article.count.toString(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(90.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Icono de editar en negro
                    IconButton(onClick = onModifyCountClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modificar cantidad",
                            tint = Color.Black
                        )
                    }
                }
            }

            if (canMove) {
                DefaultButton(
                    contentText = "Mover",
                    onClick = { onMoveArticle(article) },
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun MovementsContent(movementsList: List<ArticleMovement>) {
    if (movementsList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay movimientos disponibles",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
        return
    }
        MovementsList(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            movementsList = movementsList
        )
}

@Composable
fun ZoneInfoTopBar(
    uiState: ZoneInfoUiState,
    isSharedZone: Boolean,
    isInArticlePage: Boolean,
    onBack: () -> Unit,
    onShareZone: () -> Unit,
    onSaveZone: () -> Unit,
    onEditZoneName : () -> Unit,
    onToggleSelectionMode: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Botón de volver
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Nombre de la zona + icono de tipo
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.zone?.name ?: "Zona desconocida",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 🔥 BOTÓN EDITAR (LÁPIZ)
            IconButton(
                onClick = onEditZoneName
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar nombre",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = if (isSharedZone) Icons.Default.Group else Icons.Default.Lock,
                contentDescription = if (isSharedZone) "Zona compartida" else "Zona local",
                tint = if (isSharedZone) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }

        // Botones de acción al final
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de compartir (solo si es zona compartida)
            if (isSharedZone) {
                IconButton(onClick = onShareZone) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Compartir zona"
                    )
                }
            }

            // Botón de guardar (si hay datos para guardar)
            if (uiState.articlesToSaveList.isNotEmpty() || uiState.movementsToSaveList.isNotEmpty()) {
                IconButton(onClick = onSaveZone) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = "Guardar zona"
                    )
                }
            }

            // Botón de selección de artículos (solo en página de artículos)
            if (isInArticlePage) {
                IconButton(onClick = onToggleSelectionMode) {
                    Icon(
                        imageVector = if (uiState.selectionRemoveModeState) Icons.Default.Close else Icons.Default.CheckBox,
                        contentDescription = if (uiState.selectionRemoveModeState) "Salir de selección" else "Seleccionar elementos",
                        tint = if (uiState.selectionRemoveModeState) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}




@Composable
fun ZoneBreadcrumbs(
    uiState: ZoneInfoUiState,
    onNavigateToZone: (zoneId: String) -> Unit
) {
    val zone = uiState.zone
    val route = uiState.childZoneSummaryList
    if (zone == null || zone.parentIdList.isNullOrEmpty()) return

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            route.forEachIndexed { index, summary ->
                Text(
                    text = summary.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1565C0),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable { onNavigateToZone(summary.id) }
                )
                Text(
                    text = " < ",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = zone.name,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
}


@Composable
fun SvgPickerHandler(
    openPicker: Boolean,
    zoneId: String,
    onArticlesParsed: (List<Article>) -> Unit,
    onShowToast: (String) -> Unit,
    onOpenHandled: () -> Unit
) {
    val context = LocalContext.current

    // Launcher del picker
    val svgLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.let {
                    val articles = parseArticlesFromSVG(it, zoneId)
                    onArticlesParsed(articles)
                    it.close()
                }
            } catch (e: Exception) {
                onShowToast("Error al leer el SVG: ${e.message}")
            }
        } else {
            onShowToast("No se seleccionó ningún archivo")
        }
    }

    // Abrir picker cuando openPicker sea true
    LaunchedEffect(openPicker) {
        if (openPicker) {
            svgLauncher.launch("image/svg+xml")
            onOpenHandled()
        }
    }
}


@Composable
private fun <T : BaseZonePages> HorizontalZonePagerWithState(
    pagerState: PagerState,
    pagerItems: List<T>,
    contentForPage: @Composable (page: T) -> Unit
) {
    val scope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(
                    tabPositions[pagerState.currentPage]
                ), height = 3.dp
            )
        }
    ) {
        pagerItems.forEachIndexed { index, page ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                text = { Text(page.title) })
        }
    }

    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
        contentForPage(pagerItems[pageIndex])
    }
}
