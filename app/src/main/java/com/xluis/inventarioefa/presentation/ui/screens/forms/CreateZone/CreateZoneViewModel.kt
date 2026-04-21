package com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.GetUserLoggedEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.CreateZoneFirestoreCase
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneNameById
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetUserZonesSummary
import com.xluis.inventarioefa._domain.UseCases.Room.Article.CreateArticleCase
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.CreateZoneWithParentRoomCase
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZone
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZoneList
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.data.Mapper.Article.toFirestore
import com.xluis.inventarioefa.data.Mapper.toFirestore
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateZoneViewModel(
    private val createZoneCase: CreateZoneFirestoreCase,
    private val createZoneWithParentRoomCase: CreateZoneWithParentRoomCase,
    private val createArticleCase: CreateArticleCase,
    private val getUserZonesSummary: GetUserZonesSummary,
    private val getUserLoggedEmail: GetUserLoggedEmail,
    private val getRoomZone: GetRoomZone,
    private val getFirestoreZoneNameById: GetFirestoreZoneNameById,
    private val getRoomZoneList: GetRoomZoneList
) : ViewModel() {

    private val _uiState = mutableStateOf(CreateZoneUiState())
    val uiState: State<CreateZoneUiState> = _uiState

    private val _uiEffect = Channel<CreateZoneUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()

    // ---------------------------
    // Función genérica para actualizar el UI State
    // ---------------------------
    private fun updateState(update: CreateZoneUiState.() -> CreateZoneUiState) {
        _uiState.value = _uiState.value.update()
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
            }
        }
    }


    // ---------------------------
    // Eventos
    // ---------------------------
    fun onEvent(event: CreateZoneUiEvent) {
        when (event) {
            is CreateZoneUiEvent.IconChanged -> updateState { copy(icon = event.icon) }
            is CreateZoneUiEvent.ZoneNameChanged -> updateState { copy(zoneName = event.zoneName) }
            is CreateZoneUiEvent.ZoneParentIdChanged -> updateState { copy(parentZoneId = event.parentId) }
            is CreateZoneUiEvent.CreateZoneClicked -> {
                if(_uiState.value.zoneName.isBlank()) {
                    showToast("Rellena el nombre de la zona")
                    return
                }
                val zone = Zone(
                    name = uiState.value.zoneName,
                    ownerId = _uiState.value.userId,
                    storageType = uiState.value.storageType,
                    parentIdList = uiState.value.parentZoneId?.let { listOf(it) },
                    articleList = event.articleSelectedList
                )
                val parentId = uiState.value.parentZoneId

                viewModelScope.launch {
                    updateState { copy(isLoading = true) }
                    createZoneWithParent(zone, parentId)
                        .onSuccess {
                            showToast("Zona creada correctamente")
                            updateState { copy(isLoading = false) }
                            navigateBack()
                        }
                        .onError { error->
                           showToast(error.message)
                        }
                    updateState { copy(isLoading = false) }
                }
            }

            CreateZoneUiEvent.NavigateBack -> navigateBack()
            CreateZoneUiEvent.OpenIconSelector -> updateState { copy(isIconSelectorOpen = true) }
            CreateZoneUiEvent.CloseIconSelector -> updateState { copy(isIconSelectorOpen = false) }

            CreateZoneUiEvent.NavigateNextPage -> {
                updateState { copy(page = page + 1) }
            }

            CreateZoneUiEvent.NavigatePreviousPage -> {
                updateState { copy(page = page - 1) }
            }

            CreateZoneUiEvent.OpenCreateArticleDialog -> updateState { copy(showCreateArticleDialog = true) }
            CreateZoneUiEvent.CloseCreateArticleDialog -> updateState { copy(showCreateArticleDialog = false) }
            is CreateZoneUiEvent.AddArticle -> saveArticleInDatabase(event.articleEntity)
            is CreateZoneUiEvent.InitParentListByUserId -> {
                updateParentList()
            }

            is CreateZoneUiEvent.InitValues -> {
                viewModelScope.launch {
                    updateState { copy(isLoading = true) }
                    val parentName = getParentNameByStorageType(event.storageType, event.parentId)
                    updateState { copy(isLoading = false) }

                    updateState {
                        copy(
                            parentZoneId = event.parentId,
                            isParentPredefined = event.parentId != null,
                            storageType = event.storageType,
                            parentName = parentName,
                        )
                    }
                }
            }

            is CreateZoneUiEvent.ShowToast -> showToast(event.message)
        }
    }

    private fun updateParentList() {
        viewModelScope.launch {
            // Activar loading
            updateState { copy(isLoading = true) }

            try {
                val userId = _uiState.value.userId ?: run {
                    showToast("No se ha encontrado el usuario")
                    return@launch
                }

                val zoneSummaryList: List<ZoneSummary> = when (_uiState.value.storageType) {

                    StorageType.LOCAL -> {
                        var list: List<ZoneSummary> = emptyList()
                        getRoomZoneList(userId)
                            .onSuccess { roomList ->
                                list = roomList.map {
                                    ZoneSummary(it.id ?: "", it.name)
                                }
                            }
                            .onError { error ->
                                showToast(error.message)
                            }
                        list
                    }

                    StorageType.FIREBASE -> {
                        val email = getUserLoggedEmail() ?: run {
                            showToast("No se ha encontrado el email del usuario")
                            return@launch
                        }

                        var list: List<ZoneSummary> = emptyList()
                        getUserZonesSummary(email)
                            .onSuccess { firebaseList ->
                                list = firebaseList
                            }
                            .onError { error ->
                                showToast(error.message)
                            }
                        list
                    }
                }

                // Actualizar lista de padres
                updateState { copy(parentList = zoneSummaryList) }

            } finally {
                // Desactivar loading siempre, aunque falle
                updateState { copy(isLoading = false) }
            }
        }
    }


    private suspend fun getParentNameByStorageType(
        storageType: StorageType,
        parentId: String?
    ): String {
        if (parentId == null) return "Sin nombre"

        // Activar loading
        changeLoadingTo(true)

        try {
            var name: String = "Sin nombre"

            when (storageType) {
                StorageType.LOCAL -> {
                    getRoomZone(parentId)
                        .onSuccess { zone ->
                            name = zone?.name ?: "Sin nombre"
                        }
                        .onError { error ->
                            name = "Error: ${error.message}"
                            showToast(error.message)
                        }
                }

                StorageType.FIREBASE -> {
                    getFirestoreZoneNameById(parentId)
                        .onSuccess { zoneName ->
                            name = zoneName
                        }
                        .onError { error ->
                            name = "Error: ${error.message}"
                            showToast(error.message)
                        }
                }
            }

            return name
        } finally {
            // Desactivar loading siempre
            changeLoadingTo(false)
        }
    }





    private fun saveArticleInDatabase(articleEntity: ArticleEntity) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            createArticleCase(articleEntity)
                .onSuccess {
                    showToast("Articulo guardado correctamente")
                }
                .onError { error->
                    showToast(error.message)
                }

            updateState { copy(isLoading = false) }
        }
    }




    private suspend fun createZoneWithParent(
        child: Zone,
        parentId: String?
    ): ValidationResult {
        // Activar loading
        changeLoadingTo(true)

        try {
            val state = uiState.value

            // --- VALIDACIONES ---
            validateZone(child).let {
                if (it is ValidationResult.Error) return it
            }

            val userId = state.userId

            if (child.storageType == StorageType.FIREBASE && userId == null) {
                return ValidationResult.Error("Comprueba que has iniciado sesión")
            }

            val parentIdLong = parentId?.toLongOrNull()

            // --- CREACIÓN ---
            return when (child.storageType) {
                StorageType.LOCAL -> {
                    createZoneWithParentRoomCase(
                        child = child,
                        parentId = parentIdLong
                    )
                }
                StorageType.FIREBASE -> {
                    createZoneCase(
                        zone = child.toFirestore().copy(ownerId = userId),
                        articleList = child.articleList.map { it.toFirestore() },
                        parentId = parentId,
                        userId = userId!!
                    )
                }
            }
        } finally {
            // Desactivar loading aunque falle o tenga éxito
            changeLoadingTo(false)
        }
    }

    // Función para actualizar el state
    private fun changeLoadingTo(loadingState: Boolean) {
        viewModelScope.launch {
            updateState { copy(isLoading = loadingState) }
        }
    }

    private fun validateZone(
        zone: Zone
    ): ValidationResult {
        if (zone.name.isBlank()) {
            return ValidationResult.Error("El nombre de la zona no puede estar vacio")
        }
        return ValidationResult.Success
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.send(CreateZoneUiEffect.NavigateBack)
        }
    }



    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(CreateZoneUiEffect.ShowToast(message))
        }
    }
}
