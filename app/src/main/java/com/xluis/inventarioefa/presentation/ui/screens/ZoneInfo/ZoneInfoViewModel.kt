package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.GetUserLoggedEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.GetUserIdByEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.GetUserNameById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserLoggedUsername
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.SendZoneUserRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.RemoveZoneMember
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.ChangeZoneName
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetArticlesByZoneId
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetMovementsByZoneId
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetZoneById
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetZoneNameById
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.InsertMovements
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.RemoveArticlesByIdList
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.SaveDatabaseChanges
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.UpdateArticleCount
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.UpdateDescription
import com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected.ClearAllArticleAndMovementSelected
import com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected.GetArticlesByScreenAndZoneIds
import com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected.RemoveArticleSelectedListByIds
import com.xluis.inventarioefa._domain.UseCases.Room.MovementSelected.GetMovementsByScreenAndZoneIds
import com.xluis.inventarioefa._domain.UseCases.Room.RemoveArticlesAndMovementByArticleId
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.model.Zone.ZoneMember
import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa._domain.util.orThrow
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.data.Model.Firestore.User.ZoneRequestFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class ZoneInfoViewModel(
    private val getZoneById: GetZoneById,
    private val getArticlesByZoneId: GetArticlesByZoneId,
    private val sendZoneUserRequest: SendZoneUserRequest,
    private val removeZoneMember: RemoveZoneMember,
    private val getUserIdByEmail: GetUserIdByEmail,
    private val getUserNameById: GetUserNameById,
    private val getUserLoggedEmail: GetUserLoggedEmail,
    private val getZoneNameById: GetZoneNameById,
    private val getFirestoreUsername: GetUserLoggedUsername,
    private val saveDatabaseChanges: SaveDatabaseChanges,
    private val getMovementsByZoneId: GetMovementsByZoneId,
    private val removeArticlesByIdList: RemoveArticlesByIdList,
    private val insertMovements: InsertMovements,
    private val updateArticleCount: UpdateArticleCount,
    private val getArticlesByScreenAndZoneIds: GetArticlesByScreenAndZoneIds,
    private val getMovementsByScreenAndZoneIds: GetMovementsByScreenAndZoneIds,
    private val removeArticleSelectedListByIds: RemoveArticleSelectedListByIds,
    private val clearAllArticleAndMovementSelected: ClearAllArticleAndMovementSelected,
    private val removeArticlesAndMovementByArticleId: RemoveArticlesAndMovementByArticleId,
    private val getUserLoggedUsername: GetUserLoggedUsername,
    private val changeZoneName: ChangeZoneName,
    private val updateDescription: UpdateDescription
) : ViewModel() {

    private val _uiState = mutableStateOf(ZoneInfoUiState())
    val uiState: State<ZoneInfoUiState> = _uiState

    private val _uiEffect = Channel<ZoneInfoUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

    private var articlesCollectorJob: Job? = null
    private var movementsCollectorJob: Job? = null

    private fun updateState(update: ZoneInfoUiState.() -> ZoneInfoUiState) {
        _uiState.value = _uiState.value.update()
    }

    private fun sendUiEffect(effect: ZoneInfoUiEffect) {
        viewModelScope.launch {
            _uiEffect.send(effect)
        }
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
            }

        }
    }

    fun onEvent(event: ZoneInfoUiEvent) {
        when (event) {
            is ZoneInfoUiEvent.UpdateZone -> updateZoneById(event.zoneId)
            ZoneInfoUiEvent.NavigateToArticleSelector -> sendUiEffect(ZoneInfoUiEffect.NavigateToArticleSelector)
            is ZoneInfoUiEvent.NavigateToZone -> sendUiEffect(ZoneInfoUiEffect.NavigateToZone(event.zoneId))
            ZoneInfoUiEvent.NavigateToZoneSelector -> sendUiEffect(ZoneInfoUiEffect.NavigateToZoneSelector)
            ZoneInfoUiEvent.NavigateBack -> sendUiEffect(ZoneInfoUiEffect.NavigateBack)
            ZoneInfoUiEvent.SaveChanges -> saveChanges()
            is ZoneInfoUiEvent.AddMemeberByEmail -> addMemberByEmail(event.email)
            ZoneInfoUiEvent.ShowAddMovementMenu -> updateState { copy(isFabAddMovementsMenuOpen = true) }
            ZoneInfoUiEvent.DissmissAddMovementMenu -> updateState { copy(isFabAddMovementsMenuOpen = false) }
            ZoneInfoUiEvent.ShowConfirmLeftDialog -> updateState { copy(showConfirmLeftDialog = true) }
            ZoneInfoUiEvent.DissmissConfirmLeftDialog -> updateState { copy(showConfirmLeftDialog = false) }
            ZoneInfoUiEvent.ShowQuantityDialog -> updateState { copy(showQuantityDialog = true) }
            ZoneInfoUiEvent.DissmissQuantityDialog -> updateState { copy(showQuantityDialog = false) }
            is ZoneInfoUiEvent.AddRemoveQuantity -> {
                val delta = if (event.isAdd) event.quantity else -event.quantity
                updateArticleQuantity(delta)
            }

            ZoneInfoUiEvent.InitToSaveListsFlows -> {
                val state = _uiState.value
                val screenId = state.screenId
                val zoneId = state.zone?.id

                if (screenId.isNullOrBlank() || zoneId.isNullOrBlank()) {
                    showToast("No se han definido screenId o zoneId")
                    return
                }
                initFlows(screenId, zoneId)
            }

            is ZoneInfoUiEvent.SelectArticle -> {
                val currentList = _uiState.value.selectionArticleToRemove.toMutableList()
                val articleId = event.articleId
                if (!currentList.contains(articleId)) {
                    currentList.add(articleId)
                    updateState { copy(selectionArticleToRemove = currentList) }
                }
            }

            is ZoneInfoUiEvent.ToggleConfirmDescriptionDialogState -> {
                updateState { copy(showConfirmDeleteByDescriptionDialog = event.state) }
            }

            is ZoneInfoUiEvent.SelectDescriptionToDelete -> updateState {
                copy(
                    descriptionToDeleteSelected = event.description
                )
            }

            ZoneInfoUiEvent.ShowDeleteArticleDialog -> {
                val state = _uiState.value

                val selectedIds = state.selectionArticleToRemove

                if (selectedIds.isEmpty()) {
                    showToast("Selecciona algún artículo")
                    return
                }

                updateState {
                    copy(
                        showDeleteArticlesDialog = true
                    )
                }
            }

            ZoneInfoUiEvent.DismissDeleteArticleDialog -> {
                updateState {
                    copy(
                        showDeleteArticlesDialog = false
                    )
                }
            }


            is ZoneInfoUiEvent.DeselectArticle -> {
                val currentList = _uiState.value.selectionArticleToRemove.toMutableList()
                if (currentList.contains(event.idDeselect)) {
                    currentList.remove(event.idDeselect)
                    updateState { copy(selectionArticleToRemove = currentList) }
                }
            }

            is ZoneInfoUiEvent.InitValues -> updateState {
                copy(
                    storageType = StorageType.fromName(
                        event.storageType
                    ),
                    screenId = event.screenId
                )
            }

            is ZoneInfoUiEvent.ToggleSelectionDeleteArticleMode -> updateState {
                val newState = when {
                    event.state && isAnyArticle() -> true
                    event.state && !isAnyArticle() -> {
                        showToast("No hay ningún artículo para seleccionar")
                        selectionRemoveModeState
                    }

                    else -> false
                }

                copy(
                    selectionRemoveModeState = newState
                )
            }


            ZoneInfoUiEvent.InitArticlesAndMovementsList -> {
                viewModelScope.launch {
                    updateState { copy(isLoading = true) }
                    getArticleList()
                    getMovementList()
                    updateState { copy(isLoading = false) }

                }
            }

            is ZoneInfoUiEvent.ToggleShareDialogState -> updateState { copy(shareDialogState = event.state) }
            is ZoneInfoUiEvent.ShowToast -> showToast(event.message)
            is ZoneInfoUiEvent.RemoveMember -> _uiState.value.zoneMemberToRemove?.let { member ->
                removeMember(member.id)
            }

            ZoneInfoUiEvent.OpenSVGSelector -> {
                openSVGSelectorCall()
            }


            is ZoneInfoUiEvent.RemoveArticlesById -> {
                removeSelectedArticles()
            }


            is ZoneInfoUiEvent.MoveSelectedArticle -> {
                navigateToZoneSelectorEffect()
            }

            ZoneInfoUiEvent.ClearArticleDeleteSelections -> clearArticleDeleteSelection()

            ZoneInfoUiEvent.NavigateBackShowDialog -> showNavigateBackLogicDialog()

            is ZoneInfoUiEvent.UpdateArticlesToSaveList -> updateState { copy(articlesToSaveList = event.articleToSaveList) }
            is ZoneInfoUiEvent.UpdateMovementsToSaveList -> updateState { copy(movementsToSaveList = event.movementsToSaveList) }
            ZoneInfoUiEvent.ClearToSaveLists -> {
                viewModelScope.launch {
                    clearToSaveList()
                }
            }

            is ZoneInfoUiEvent.RemoveArticleToSave -> {
                val state = _uiState.value
                if (!state.articlesToSaveList.map { it.id }.contains(event.articleId)) {
                    removeArticleList(listOf(event.articleId))
                }
            }

            is ZoneInfoUiEvent.AddMovementToSaveList -> {
                addMovementToSaveList(event.movementList)
            }

            is ZoneInfoUiEvent.ChangeMovementCount -> {
                changeMovementCount(event.movementId, event.newCount)
            }

            is ZoneInfoUiEvent.AddArticleToSave -> {
                addArticleToSave(event.article)
            }

            is ZoneInfoUiEvent.RemoveArticleInSaveList -> {
                removeArticleInSaveList(event.article.id)
            }

            is ZoneInfoUiEvent.RemoveMovementInSaveList -> {
                removeMovementInSaveList(event.movementId)
            }

            is ZoneInfoUiEvent.QuantityChanged -> {
                quantityChange(event.article)
            }

            ZoneInfoUiEvent.RemoveArticlesToSave -> {
                viewModelScope.launch {
                    clearToSaveList()
                }
            }

            is ZoneInfoUiEvent.ToggleShowMoveDialog -> {
                updateState {
                    copy(showMoveDialog = event.state)
                }
            }

            is ZoneInfoUiEvent.SetMoveArticle -> {
                updateState {
                    copy(articleToMoveSelected = event.articleToMove)
                }
            }

            is ZoneInfoUiEvent.SetMoveCount -> {
                updateState {
                    copy(articleToMoveSelected = articleToMoveSelected?.copy(count = event.newCount))
                }
            }

            is ZoneInfoUiEvent.UpdateDescription -> {
                updateDescriptionByOld(event.oldDescription, event.newDescription)
            }

            is ZoneInfoUiEvent.UpdateArticleToModifyQuantity -> {
                updateState { copy(articleToModifyCountSelected = event.articleToModify) }
            }

            is ZoneInfoUiEvent.ShowRemoveMemberDialog -> {
                updateState {
                    copy(
                        showDeleteMemberDialog = true,
                        zoneMemberToRemove = event.memberSelected
                    )
                }
            }

            ZoneInfoUiEvent.DissmissRemoveMemberDialog -> {
                updateState { copy(showConfirmLeftDialog = false, zoneMemberToRemove = null) }
            }

            ZoneInfoUiEvent.ShowZoneNameDialog -> updateState { copy(showChangeZoneNameDialog = true) }
            ZoneInfoUiEvent.DismissZoneNameDialog -> updateState { copy(showChangeZoneNameDialog = false) }
            is ZoneInfoUiEvent.ChangeZoneName -> changeZoneNameByStorageType(event.newZoneName)

            is ZoneInfoUiEvent.SelectArticleDescription -> {
                updateState { copy(selectedArticleDescription = event.article) }
            }

            is ZoneInfoUiEvent.ToggleDescriptionDialog -> {
                updateState { copy(showDescriptionDialog = event.state) }
            }

            ZoneInfoUiEvent.DeleteDescription -> {
                deleteDescription()
            }

            else -> {}
        }
    }

    private fun updateDescriptionByOld(
        oldDescription: String,
        newDescription: String
    ) {
        val state = _uiState.value
        val articleId = state.selectedArticleDescription?.id ?: return
        val storageType = state.storageType
        val zoneId = state.zone?.id ?: return

        viewModelScope.launch {
            updateDescription(
                articleId = articleId,
                zoneId = zoneId,
                storageType = storageType,
                oldDescription = oldDescription,
                newDescription = newDescription
            ).onSuccess {
                showToast("Descripción actualizada")
            }
                .onError {
                    showToast(it.message)
                }
        }
    }

    private fun deleteDescription() {
        val state = _uiState.value
        val description = state.descriptionToDeleteSelected
        val article = state.selectedArticleDescription ?: return

        val zoneId = state.zone?.id ?: return
        val storageType = state.storageType

        // 1. quitar descripción
        val newDescriptions = article.descriptions.filter { it != description }

        viewModelScope.launch {

            // CASO 1: si count == 1 → eliminar artículo entero
            if (article.count <= 1) {
                removeArticlesByIdList(
                    zoneId = zoneId,
                    articleIdList = listOf(article.id),
                    storageType = storageType
                ).onSuccess {
                    updateState {
                        copy(
                            articleList = articleList.filter { it.id != article.id },
                            selectedArticleDescription = null,
                            descriptionToDeleteSelected = null
                        )
                    }
                    showToast("Artículo eliminado")
                }.onError {
                    showToast(it.message)
                }

                return@launch
            }

            // CASO 2: count > 1 → solo bajar count y quitar descripción
            val updatedArticle = article.copy(
                descriptions = newDescriptions,
                count = article.count - 1
            )

            updateState {
                copy(
                    selectedArticleDescription = updatedArticle,
                    articleList = articleList.map {
                        if (it.id == article.id) updatedArticle else it
                    }
                )
            }

            updateArticleCount(
                zoneId = zoneId,
                storageType = storageType,
                articleToUpdate = updatedArticle
            )
        }
    }

    private fun changeZoneNameByStorageType(newZoneName: String) {
        val state = _uiState.value
        val zoneId = state.zone?.id ?: run {
            showToast("No se ha encontrado la zona")
            return
        }
        val storageType = state.storageType

        if (storageType == StorageType.FIREBASE && zoneId == state.zone.ownerId) {
            showToast("No puedes cambiar el nombre de la zona sin ser el propietario")
            return
        }

        viewModelScope.launch {
            try {
                updateState { copy(isLoading = true) }
                changeZoneName(zoneId, newZoneName, storageType)
                    .onSuccess {
                        showToast("El nombre de la zona a cambiado correctamente")
                        updateZoneById(zoneId)
                    }
                    .onError { error -> showToast(error.message) }
            } finally {
                updateState {
                    copy(
                        isLoading = false,
                        showChangeZoneNameDialog = false
                    )
                }
            }
        }
    }

    private fun updateArticleQuantity(delta: Int) {
        val state = _uiState.value
        val article = state.articleToModifyCountSelected ?: return
        val currentCount = article.count
        val zoneId = state.zone?.id ?: return
        val zoneName = state.zone.name
        val userId = state.userId ?: return
        val storageType = state.storageType

        val newCount = currentCount + delta

        // Validaciones básicas
        if (delta < 0 && newCount < 0) {
            showToast("No se puede eliminar más cantidad de la existente")
            return
        }
        if (delta > 0 && newCount < 0) {
            showToast("Cantidad inválida")
            return
        }

        viewModelScope.launch {
            val userName = getUserLoggedUsername(userId).getOrNull() ?: "???"

            // Determinar si se elimina o se actualiza
            val operation = if (delta < 0 && newCount <= 0) {
                // eliminar artículo porque no queda stock
                removeArticlesByIdList(zoneId, listOf(article.id), storageType)
            } else {
                // actualizar cantidad restante
                updateArticleCount(zoneId, storageType, article.copy(count = newCount))
            }

            operation.onSuccess {
                // Mensaje
                if (delta < 0 && newCount <= 0) {
                    showToast("Artículo eliminado")
                } else {
                    showToast("Cantidad actualizada")
                }

                // Crear movimiento
                val movement = ArticleMovement(
                    articleId = article.id,
                    zoneId = zoneId,
                    zoneName = zoneName,
                    articleName = article.name,
                    userId = userId,
                    userName = userName,
                    count = if (delta < 0 && newCount <= 0) currentCount else delta,
                    actionType = if (delta < 0) MovementAction.TAKE else MovementAction.ADD
                )

                insertMovements(zoneId, listOf(movement), storageType)
            }
                .onError { error ->
                    showToast(error.message)
                }
        }
    }


    private fun clearArticleDeleteSelection() {
        updateState {
            copy(
                selectionArticleToRemove = emptyList(),
                selectionRemoveModeState = false
            )
        }
    }

    private fun initFlows(screenId: String, zoneId: String) {

        articlesCollectorJob?.cancel()
        articlesCollectorJob = viewModelScope.launch {
            getArticlesByScreenAndZoneIds(screenId, zoneId)
                .collect { articles ->
                    updateState { copy(articlesToSaveList = articles.map { it.toDomain() }) }
                }
        }

        movementsCollectorJob?.cancel()
        movementsCollectorJob = viewModelScope.launch {
            getMovementsByScreenAndZoneIds(screenId, zoneId)
                .collect { movements ->
                    updateState { copy(movementsToSaveList = movements.map { it.toDomain() }) }
                }
        }
    }

    private fun quantityChange(article: Article) {
        val state = _uiState.value
        val originalCount = state.articleCountsMap[article.id] ?: 0
        val currentCount = article.count

        // Verificar si zoneId existe
        val zoneId = state.zone?.id ?: run {
            showToast("No se ha encontrado la zona")
            return
        }
        val storageType = state.storageType

        val diff = currentCount - originalCount

        // Si no hay cambio, eliminar movimientos existentes y salir
        if (diff == 0) {
            removeArticleInSaveList(article.id)
            val movementIdToRemove =
                state.movementsToSaveList.firstOrNull { it.articleId == article.id }?.id
            movementIdToRemove?.let { removeMovementInSaveList(it) }
            return
        }

        val action = if (diff > 0) MovementAction.ADD else MovementAction.TAKE
        val count = kotlin.math.abs(diff)

        // Crear el movimiento directamente y guardarlo
        val movement = ArticleMovement(
            articleId = article.id,
            articleName = article.name,
            zoneId = state.zone.id.orEmpty(),
            zoneName = state.zone.name,
            userId = state.userId.orEmpty(),
            count = count,
            actionType = action
        )

        // 🔹 Actualizar el map de cantidades1
        updateState { copy(articleCountsMap = state.articleCountsMap + (article.id to currentCount)) }

        // 🔹 Actualizar la cantidad del artículo en la zona
        viewModelScope.launch {
            insertMovements(zoneId, listOf(movement), storageType)
            updateArticleCount(zoneId, storageType, article)
                .onSuccess { showToast("Artículo actualizado") }
                .onError { showToast("No se ha podido actualizar el artículo") }
        }
    }


    private fun removeSelectedArticles() {
        val state = _uiState.value
        val articleIdListToRemove = state.selectionArticleToRemove
        val articleToSaveList = state.articlesToSaveList
        val storageType = state.storageType
        val zoneId = state.zone?.id ?: run {
            showToast("No se ha encontrado la zona")
            return
        }

        if (articleIdListToRemove.isEmpty()) {
            showToast("Selecciona algún artículo")
            return
        }

        updateState { copy(isLoading = true) }

        viewModelScope.launch {
            val result: Result<Unit> = runCatching {

                val articleToSaveIds = articleToSaveList.map { it.id }

                // Artículos no persistidos
                val articlesToSaveSelected =
                    articleIdListToRemove.filter { it in articleToSaveIds }

                // Artículos ya guardados
                val articlesStored =
                    articleIdListToRemove.filter { it !in articleToSaveIds }

                // ===============================
                // 2️⃣ INSERTAR movimientos
                // ===============================
                if (articlesStored.isNotEmpty()) {
                    val movements = state.articleList
                        .filter { it.id in articlesStored }
                        .map { article ->
                            ArticleMovement(
                                articleId = article.id,
                                articleName = article.name,
                                zoneId = state.zone.id ?: "",
                                zoneName = state.zone.name ?: "???",
                                count = article.count,
                                actionType = MovementAction.TAKE,
                                userId = state.userId ?: ""
                            )
                        }

                    insertMovements(zoneId, movements, storageType).orThrow()
                }

                // ===============================
                // 1️⃣ ELIMINAR artículos persistidos
                // ===============================
                if (articlesStored.isNotEmpty()) {
                    removeArticlesByIdList(zoneId, articlesStored, storageType).orThrow()
                }

                // ===============================
                // 3️⃣ ELIMINAR artículos no persistidos
                // ===============================
                if (articlesToSaveSelected.isNotEmpty()) {
                    removeArticleList(articlesToSaveSelected)
                }
            }

            result
                .onSuccess { handleArticlesRemoved(articleIdListToRemove, state.articleList) }
                .onFailure { error ->
                    showToast(error.message ?: "Error eliminando artículos")
                    updateState { copy(isLoading = false) }
                }
        }
    }

    private fun handleArticlesRemoved(
        articleIdListToRemove: List<String>,
        articleList: List<Article>
    ) {
        updateState {
            copy(
                articleList = articleList.filter { it.id !in articleIdListToRemove },
                selectionArticleToRemove = emptyList(),
                selectionRemoveModeState = false,
                showDeleteArticlesDialog = false,
                isLoading = false,
                articlesToSaveList = articlesToSaveList.filter { it.id !in articleIdListToRemove },
                movementsToSaveList = movementsToSaveList.filter { it.articleId !in articleIdListToRemove }
            )
        }

        showToast("Se han eliminado los artículos correctamente")
        _uiState.value.zone?.id?.let { updateZoneById(it) }
    }


    private fun getArticleList() {
        val state = _uiState.value
        val zoneId = state.zone?.id ?: run {
            showToast("No se ha encontrado la zona")
            return
        }

        when (val result = getArticlesByZoneId(zoneId, state.storageType)) {

            is SuspendResult.Success -> {
                viewModelScope.launch {
                    result.data
                        .catch { e -> showToast(e.message ?: "Error al cargar los artículos") }
                        .collect { articles ->
                            updateArticleState(articles)
                        }
                }
            }

            is SuspendResult.Error -> {
                showToast(result.message)
            }

            else -> {}
        }
    }

    private fun updateArticleState(articles: List<Article>) {
        val countsMap = articles.associate { it.id to it.count }
        updateState {
            copy(
                articleList = articles,
                articleCountsMap = countsMap
            )
        }

    }


    private fun removeMember(memberId: String) {
        val zoneId = _uiState.value.zone?.id

        if (zoneId == null) {
            showToast("No se ha encontrado la zona")
            return
        }
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                removeZoneMember(zoneId, memberId)
                    .onSuccess {
                        updateZoneById(zoneId)
                        showToast("Miembro eliminado correctamente")
                    }
                    .onError { error -> showToast(error.message) }
            } finally {
                updateState {
                    copy(
                        showDeleteMemberDialog = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun getMovementList() {
        val state = _uiState.value
        val zone = state.zone ?: run {
            showToast("No se ha encontrado la zona")
            return
        }
        val storageType = zone.storageType

        viewModelScope.launch {
            // Obtener movimientos de la zona principal
            getMovementsByZoneId(zone.id!!, storageType)
                .catch { e ->
                    showToast(e.message ?: "Error al cargar los movimientos")
                }
                .collect { movements ->
                    val allMovements = movements.toMutableList()

                    // Agregar movimientos de zonas hijas
                    zone.childIdList.orEmpty().forEach { childId ->
                        val childMovements = getParentMovements(childId, storageType)
                        allMovements.addAll(childMovements)
                    }

                    // Actualizar estado ordenado por fecha descendente
                    updateState {
                        copy(movementList = allMovements.sortedByDescending { it.date })
                    }
                }
        }
    }


    private suspend fun getParentMovements(
        parentId: String,
        storageType: StorageType
    ): List<ArticleMovement> {
        return getMovementsByZoneId(parentId, storageType)
            .catch { emit(emptyList()) }
            .first()
    }


    private fun addMemberByEmail(email: String) {
        viewModelScope.launch {
            val state = _uiState.value

            if (email == getUserLoggedEmail()) {
                showToast("No te puedes enviar la solicitud a ti mismo")
                return@launch
            }

            val zoneId = state.zone?.id
                ?: return@launch showToast("No se ha encontrado el identificador de la zona")

            val userId = state.userId
                ?: return@launch showToast("Usuario no identificado")


            /*
            val receiverId = getUserIdByEmail(email)
                .getOrNull()
                ?: return@launch showToast("No se ha encontrado el usuario")

             */
            // val requesterName = getUserNameById(userId).getOrNull().orEmpty()

            val receiverId =
                getUserIdByEmail(email)
                    .onError { showToast(it.message) }.getOrNull().orEmpty()

            val requesterName =
                getUserNameById(userId).onError { showToast(it.message) }.getOrNull().orEmpty()


            val request = ZoneRequestFirestore(
                zoneId = zoneId,
                zoneName = state.zone.name,
                requesterId = userId,
                requesterName = requesterName,
                receiverId = receiverId,
                createdAt = Timestamp.now()
            )

            sendZoneUserRequest(request)
                .onSuccess { showToast("La solicitud se ha enviado correctamente") }
                .onError { showToast(it.message) }
        }
    }


    fun updateZoneById(zoneId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            val storageType = _uiState.value.storageType

            getZoneById(zoneId, storageType)
                .onSuccess { zone ->
                    loadZoneData(
                        zone,
                        isFirebaseZone = storageType == StorageType.FIREBASE
                    )
                }
                .onError { showToast(it.message) }

            updateState { copy(isLoading = false) }
        }
    }

    private suspend fun loadZoneData(zone: Zone, isFirebaseZone: Boolean) {
        // 1️⃣ Actualizar la zona en el estado
        updateState { copy(zone = zone) }

        // 2️⃣ Actualizar la lista de zonas hijas
        updateChildSummary(zone.childIdList ?: emptyList())

        // 3️⃣ Si la zona es de Firebase, actualizar owner y miembros
        if (isFirebaseZone && zone.ownerId != null) {
            updateOwnerAndMembersList(zone.ownerId, zone.membersId ?: emptyList())
        }

        // 4️⃣ Cargar lista de artículos
        getArticleList()

        // 5️⃣ Cargar lista de movimientos
        getMovementList()
    }


    private suspend fun updateOwnerAndMembersList(
        ownerId: String,
        membersList: List<String>
    ) {
        // Obtener owner
        getFirestoreUsername(ownerId)
            .onSuccess { ownerName ->
                updateState { copy(ownerMember = ZoneMember(ownerId, ownerName)) }
            }

        if (membersList.isEmpty()) return

        val members = coroutineScope {
            membersList.map { memberId ->
                async {
                    when (val result = getFirestoreUsername(memberId)) {
                        is SuspendResult.Success -> ZoneMember(memberId, result.data)
                        else -> null
                    }
                }
            }.awaitAll().filterNotNull()
        }

        updateState { copy(memberList = members) }

    }


    private suspend fun updateChildSummary(childIdList: List<String>) {
        // Limpiamos primero
        updateState { copy(childZoneSummaryList = emptyList()) }
        if (childIdList.isEmpty()) return

        val storageType = _uiState.value.storageType

        val childSummaryList = childIdList.mapNotNull { id ->
            val zoneNameResult = getZoneNameById(id, storageType)

            zoneNameResult.getOrNull()?.let { zoneName ->
                ZoneSummary(id, zoneName)
            }
        }

        updateState { copy(childZoneSummaryList = childSummaryList) }
    }


    private fun saveChanges() {
        viewModelScope.launch {
            val state = _uiState.value
            val zoneId = state.zone?.id ?: run {
                showToast("No se ha encontrado la zona")
                return@launch
            }

            updateState { copy(isLoading = true) }

            val articlesToSave = state.articlesToSaveList
            val movementsToSave = state.movementsToSaveList.map { it.copy(zoneId = zoneId) }
            val storageType = state.storageType

            saveDatabaseChanges(zoneId, articlesToSave, movementsToSave, storageType)
                .onSuccess {
                    val clearResult = clearToSaveList()
                    clearResult.onSuccess {
                        updateState {
                            copy(
                                articlesToSaveList = emptyList(),
                                movementsToSaveList = emptyList()
                            )
                        }
                        updateZoneById(zoneId)
                        showToast("Se han actualizado los datos correctamente")
                    }.onError { error ->
                        showToast("No se pudieron limpiar los datos: ${error.message}")
                    }
                }
                .onError { error ->
                    showToast(error.message)
                }

            updateState { copy(isLoading = false) }
        }
    }

    private fun handleSaveSuccess() {
        viewModelScope.launch {
            val state = _uiState.value
            val zoneId = state.zone?.id
            clearToSaveList()
                .onSuccess {
                    updateState {
                        copy(
                            articlesToSaveList = listOf(),
                            movementsToSaveList = listOf()
                        )
                    }
                    if (zoneId != null) updateZoneById(zoneId)
                    showToast("Se han actualizado los datos correctamente")
                }
        }
    }


    fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.ShowToast(message))
        }
    }

    private fun navigateToZoneSelectorEffect() {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.NavigateToZoneSelector)
        }
    }

    private fun openSVGSelectorCall() {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.OpenSVGSelector)
        }
    }

    private fun addArticleToSave(article: Article) {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.AddArticleToSave(article))
        }
    }

    private fun isAnyArticle(): Boolean {
        val state = _uiState.value
        return state.articleList.isNotEmpty() || state.articlesToSaveList.isNotEmpty()
    }

    private fun removeArticleList(articleIdList: List<String>) {
        viewModelScope.launch {
            val state = _uiState.value
            val zoneId = state.zone?.id ?: return@launch
            val screenId = state.screenId ?: return@launch
            removeArticleSelectedListByIds(articleIdList, zoneId, screenId)
                .onSuccess {
                    initFlows(screenId, zoneId)
                }
        }
    }

    private fun showNavigateBackLogicDialog() {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.NavigateBackShowDialog)
        }
    }

    private fun addMovementToSaveList(movementList: List<ArticleMovement>) {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.AddMovementToSaveList(movementList))
        }
    }

    private fun changeMovementCount(movementId: String, newCount: Int) {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.ChangeMovementCount(movementId, newCount))
        }
    }

    private fun removeArticleInSaveList(articleId: String) {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.RemoveArticleInSaveList(articleId))
        }
    }

    private fun removeMovementInSaveList(movementId: String) {
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.RemoveMovementInSaveList(movementId))
        }
    }


    private suspend fun clearToSaveList(): SuspendResult<Boolean> {
        val state = _uiState.value
        val screenId = state.screenId ?: return SuspendResult.Error("No hay screenId")
        val zoneId = state.zone?.id ?: return SuspendResult.Error("No hay zoneId")

        return clearAllArticleAndMovementSelected(screenId, zoneId)
    }
}

