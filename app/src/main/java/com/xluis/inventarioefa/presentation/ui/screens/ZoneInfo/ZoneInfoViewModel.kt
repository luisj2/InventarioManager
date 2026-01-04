package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

import ItemsToSave
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.GetUserLoggedEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.GetUserIdByEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserLoggedUsername
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.InsertZoneUserRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneData
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneNameById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.RemoveZoneMember
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.SaveFirestoreZoneChanges
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.GetFirestoreZoneMovementListById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.InsertMovementsInZone
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.GetFirestoreArticleListByZoneId
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.RemoveFirestoreArticleList
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article.DeleteRoomArticleList
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article.GetZoneArticlesByZoneId
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZone
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZoneNameById
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements.GetRoomZoneMovementById
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements.InsertMovementsListRoom
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.SaveRoomChanges
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.Zone.ZoneMember
import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.data.Mapper.Article.toFirestore
import com.xluis.inventarioefa.data.Mapper.Article.toZoneEntity
import com.xluis.inventarioefa.data.Mapper.toEntity
import com.xluis.inventarioefa.data.Mapper.toFirestore
import com.xluis.inventarioefa.data.Model.User.ZoneRequestFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class ZoneInfoViewModel(
    private val getFirestoreZoneData: GetFirestoreZoneData,
    private val getRoomZone: GetRoomZone,
    private val getZoneArticlesByZoneId: GetZoneArticlesByZoneId,
    private val getFirestoreArticleListByZoneId: GetFirestoreArticleListByZoneId,
    private val insertZoneUserRequest: InsertZoneUserRequest,
    private val removeZoneMember: RemoveZoneMember,
    private val getUserByEmail: GetUserIdByEmail,
    private val getUserLoggedEmail: GetUserLoggedEmail,
    private val getFirestoreZoneNameById: GetFirestoreZoneNameById,
    private val getRoomZoneNameById: GetRoomZoneNameById,
    private val getFirestoreUsername: GetUserLoggedUsername,
    private val saveRoomChanges: SaveRoomChanges,
    private val saveFirestoreZoneChanges: SaveFirestoreZoneChanges,
    private val getFirestoreZoneMovementListById: GetFirestoreZoneMovementListById,
    private val getRoomZoneMovementById : GetRoomZoneMovementById,
    private val removeFirestoreArticleList : RemoveFirestoreArticleList,
    private val deleteRoomArticleList : DeleteRoomArticleList,
    private val insertRoomMovements : InsertMovementsListRoom,
    private val insertFirestoreMovements : InsertMovementsInZone
) : ViewModel() {

    private val _uiState = mutableStateOf(ZoneInfoUiState())
    val uiState: State<ZoneInfoUiState> = _uiState

    private val _uiEffect = Channel<ZoneInfoUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

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
            is ZoneInfoUiEvent.SaveChanges -> saveChanges(event.zoneId)
            is ZoneInfoUiEvent.ShareZone -> shareZone(event.email)
            ZoneInfoUiEvent.ShowAddMovementMenu -> updateState { copy(isFabAddMovementsMenuOpen = true) }
            ZoneInfoUiEvent.DissmissAddMovementMenu -> updateState { copy(isFabAddMovementsMenuOpen = false) }
            ZoneInfoUiEvent.ShowConfirmLeftDialog -> updateState { copy(showConfirmLeftDialog = true) }
            ZoneInfoUiEvent.DissmissConfirmLeftDialog -> updateState { copy(showConfirmLeftDialog = false) }

            is ZoneInfoUiEvent.SelectArticle -> {
                val currentList = _uiState.value.selectionArticleToRemove.toMutableList()
                val articleId = event.articleId
                if (!currentList.contains(articleId)) {
                    currentList.add(articleId)
                    updateState { copy(selectionArticleToRemove = currentList) }
                }
            }

            is ZoneInfoUiEvent.AddArticleToSave -> {
                val article = event.article
                val originalArticle = _uiState.value.zone?.articleList?.firstOrNull { it.id == article.id }

                val index = ItemsToSave.articles.indexOfFirst { it.id == article.id }
                if (originalArticle != null) {
                    if (article.count != originalArticle.count) {
                        // Si ya existe, actualizamos el artículo
                        if (index >= 0) {
                            ItemsToSave.articles[index] = article
                        } else {
                            ItemsToSave.articles.add(article)
                        }
                    } else {
                        // Si no hay cambio de cantidad, lo eliminamos de la lista de guardado
                        if (index >= 0) ItemsToSave.articles.removeAt(index)
                    }
                } else {
                    // Si no existe, lo agregamos
                    if (index < 0) ItemsToSave.articles.add(article)
                }
            }

            is ZoneInfoUiEvent.RemoveArticleToSave -> {
                val articleId = event.articleId
                ItemsToSave.articles.removeIf { it.id == articleId }
            }

            is ZoneInfoUiEvent.AddMovementToSave -> {
                val movement = event.movement
                // Eliminamos cualquier movimiento existente del mismo artículo antes de agregar
                ItemsToSave.movements.removeIf { it.articleId == movement.articleId }
                ItemsToSave.movements.add(movement)
            }

            is ZoneInfoUiEvent.RemoveMovementToSave -> {
                val articleId = event.articleId
                ItemsToSave.movements.removeIf { it.articleId == articleId }
            }


            ZoneInfoUiEvent.ClearArticlesAndMovementsToSave ->{
                ItemsToSave.clear()
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

            ZoneInfoUiEvent.DismissDeleteArticleDialog ->{
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

            is ZoneInfoUiEvent.UpdateStorageType -> updateState {
                copy(
                    storageType = StorageType.fromName(
                        event.storageType
                    )
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
            is ZoneInfoUiEvent.RemoveMember -> removeMember(event.memberId)
            ZoneInfoUiEvent.OpenSVGSelector -> {
                openSVGSelectorCall()
            }


            ZoneInfoUiEvent.RemoveArticlesById -> {
                val state = _uiState.value
                val articleIdListToRemove = state.selectionArticleToRemove

                if (articleIdListToRemove.isEmpty()) {
                    showToast("Selecciona algún artículo")
                    return
                }

                updateState { copy(isLoading = true) }

                viewModelScope.launch {
                    val articlesToRemove = state.articleList.filter { it.id in articleIdListToRemove }

                    val result = when (state.storageType) {
                        StorageType.LOCAL -> {
                            val idsToRemove = articleIdListToRemove.mapNotNull { it.toLongOrNull() }
                            deleteRoomArticleList(state.zone?.id?.toLongOrNull() ?: 0L, idsToRemove)
                        }
                        StorageType.FIREBASE -> {
                            removeFirestoreArticleList(state.zone?.id ?: "", articleIdListToRemove)
                        }
                    }

                    result.onSuccess {
                        // Crear lista de movimientos
                        val movementsToInsert: List<ArticleMovement> = articlesToRemove.map { article ->
                            ArticleMovement(
                                articleId = article.id,
                                articleName = article.name,
                                zoneId = state.zone?.id ?: "",
                                zoneName = state.zone?.name ?: "???",
                                count = article.count,
                                actionType = MovementAction.TAKE,
                                userId = state.userId ?: ""
                            )
                        }

                        // Insertar los movimientos en la base de datos
                        insertMovementToDatabase(movementsToInsert)

                        // Actualizar estado de UI eliminando los artículos
                        updateState {
                            copy(
                                articleList = articleList.filter { it.id !in articleIdListToRemove },
                                selectionArticleToRemove = listOf(),
                                selectionRemoveModeState = false,
                                showDeleteArticlesDialog = false,
                                isLoading = false
                            )
                        }

                        showToast("Se han eliminado los artículos correctamente")
                        _uiState.value.zone?.id?.let { zoneId->
                            updateZoneById(zoneId)
                        }
                    }.onError { error ->
                        showToast(error.message)
                        updateState { copy(isLoading = false) }
                    }
                }
            }




            is ZoneInfoUiEvent.MoveSelectedArticle -> {
                updateState { copy(articleToMoveSelected = event.selectedArticle) }
                navigateToZoneSelectorEffect()
            }

            ZoneInfoUiEvent.ClearArticleDeleteSelections -> updateState {
                copy(
                    selectionArticleToRemove = listOf()
                )
            }

            ZoneInfoUiEvent.NavigateBackShowDialog -> showNavigateBackLogicDialog()
            else -> {}
        }
    }

    private suspend fun insertMovementToDatabase (movementList : List<ArticleMovement>) {
        when(_uiState.value.storageType){
            StorageType.LOCAL -> {
                insertRoomMovements(movementList.map { it.toEntity() })
            }
            StorageType.FIREBASE -> {
                _uiState.value.zone?.id?.let { zoneId->
                    insertFirestoreMovements(zoneId,movementList.map { it.toFirestore() })
                }
            }
        }
    }

    private suspend fun getArticleList() {
            val state = _uiState.value
            val zoneId = state.zone?.id ?: return

            when (state.storageType) {
                StorageType.LOCAL -> {
                    val zoneLongId = zoneId.toLongOrNull() ?: return
                    getZoneArticlesByZoneId(zoneLongId)
                }

                StorageType.FIREBASE -> {
                    getFirestoreArticleListByZoneId(zoneId)
                }
            }.onSuccess {
                updateState {
                    copy(
                        articleList = articleList + it
                    )
                }
            }
                .onError { error ->
                    showToast(error.message)
                }
    }




    private fun removeMember(memberId: String) {
        val zoneId = _uiState.value.zone?.id

        if (zoneId == null) {
            showToast("No se ha encontrado la zona")
            return
        }
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            removeZoneMember(zoneId, memberId)
        }
        updateState { copy(isLoading = false) }
    }

    private suspend fun getMovementList() {
        val state = _uiState.value
        val zone = state.zone ?: return


        val allMovements = zone.movementList.toMutableList()

        zone.childIdList.orEmpty().forEach { childId ->
            val childMovements = getParentMovements(childId, zone.storageType)

            allMovements.addAll(childMovements)
        }

        updateState {
            copy(movementList = allMovements.sortedByDescending { it.date })
        }
    }



    private suspend fun getParentMovements(
        parentId: String,
        storageType: StorageType
    ): List<ArticleMovement> {

        val result = when (storageType) {
            StorageType.LOCAL -> {
                val roomId = parentId.toLongOrNull() ?: return emptyList()
                getRoomZoneMovementById(roomId)
            }
            StorageType.FIREBASE -> getFirestoreZoneMovementListById(parentId)
        }

        result.onError { error ->
            showToast(error.message)
        }

        return when (result) {
            is SuspendResult.Success -> result.data
            else -> emptyList()
        }
    }


    private fun shareZone(
        email: String
    ) {
        viewModelScope.launch {
            val state = _uiState.value
            val zoneId = state.zone?.id
            val userId = state.userId

            if (zoneId == null) {
                showToast("No se ha encontrado el identificador de la zona")
                return@launch
            }

            if (userId == null) {
                showToast("Usuario no identificado")
                return@launch
            }

            if (email == getUserLoggedEmail()) {
                showToast("No te puedes enviar la solicitud a ti mismo")
                return@launch
            }

            val receiverId = getUserByEmail(email)
                .onError { error ->
                    showToast(error.message)
                }.getOrNull() ?: return@launch


            val request = ZoneRequestFirestore(
                zoneId = zoneId,
                requesterId = userId,
                receiverId = receiverId,
                createdAt = Timestamp.now()
            )

            insertZoneUserRequest(userId, request)
                .onSuccess {
                    showToast("La solicitud se ha enviado correctamente")
                }
                .onError { error ->
                    showToast(error.message)
                }
        }
    }


    fun updateZoneById(zoneId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            val storageType = _uiState.value.storageType

            val result = when (storageType) {
                StorageType.LOCAL -> getRoomZone(zoneId)
                StorageType.FIREBASE -> getFirestoreZoneData(zoneId)
            }

            result
                .onSuccess { zone ->
                    updateState { copy(zone = zone) }
                    updateChildSummary(zone?.childIdList ?: emptyList())
                    if(storageType == StorageType.FIREBASE && zone?.ownerId != null)
                        updateOwnerAndMembersList(zone.ownerId,zone.membersId ?: emptyList())
                    getArticleList()
                    getMovementList()
                }
                .onError { error ->
                    showToast(error.message)
                }

            updateState { copy(isLoading = false) }
        }
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

        // Actualizar estado solo una vez con todos los miembros
        updateState { copy(memberList = members) }
    }


    private suspend fun updateChildSummary(childIdList: List<String>) {
            updateState { copy(childZoneSummaryList = emptyList()) }
            if (childIdList.isEmpty()) return

            val childSummaryList = childIdList.mapNotNull { id ->
                when (_uiState.value.storageType) {
                    StorageType.LOCAL -> getRoomZoneNameById(id.toLongOrNull() ?: return@mapNotNull null)
                    StorageType.FIREBASE -> getFirestoreZoneNameById(id)
                }.getOrNull()?.let { zoneName -> ZoneSummary(id, zoneName) }
            }
            updateState { copy(childZoneSummaryList = childSummaryList) }
    }



    private fun saveChanges(zoneId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val state = uiState.value
            val articlesToSave = ItemsToSave.articles
            val movementsToSave = ItemsToSave.movements

            when (state.storageType) {
                StorageType.LOCAL -> {
                    val roomZoneId = zoneId.toLongOrNull() ?: run {
                        showToast("ZoneId inválido")
                        return@launch
                    }
                    saveRoomChanges(
                        articlesToSave =  articlesToSave.map { it.toZoneEntity() },
                        movementsToSave = movementsToSave.map { it.toEntity() },
                        zoneId = roomZoneId
                    )
                }

                StorageType.FIREBASE -> {
                    saveFirestoreZoneChanges(
                        zoneId = zoneId,
                        articlesToSave = articlesToSave.map { it.toFirestore() },
                        movementsToSave = movementsToSave.map { it.toFirestore() },
                    )
                }
            }.onSuccess {
                showToast("Se han actualizado los datos correctamente")
                ItemsToSave.clear()
                updateZoneById(zoneId)
            }
                .onError { error ->
                    showToast(error.message)
                }

            updateState { copy(isLoading = false) }
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

    private fun isAnyArticle(): Boolean {
        val state = _uiState.value
        return state.articleList.isNotEmpty() || ItemsToSave.articles.isNotEmpty()
    }

    private fun showNavigateBackLogicDialog(){
        viewModelScope.launch {
            _uiEffect.send(ZoneInfoUiEffect.NavigateBackShowDialog)
        }
    }

}