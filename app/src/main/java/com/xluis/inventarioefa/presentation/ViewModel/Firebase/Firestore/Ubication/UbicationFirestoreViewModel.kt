package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.Ubication
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Result.UiEvent
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.UbicationFirestoreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UbicationFirestoreViewModel(private val repository: UbicationFirestoreRepository) :
    ViewModel() {

    private val _insertUbicationStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val insertUbicationStatus: StateFlow<SuspendResult<Boolean>> = _insertUbicationStatus

    private val _allUbicationsList = MutableStateFlow<SuspendResult<List<Ubication>>>(SuspendResult.Idle)
    val allUbicationsList: StateFlow<SuspendResult<List<Ubication>>> = _allUbicationsList

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _errorMessage = MutableStateFlow("")

    fun insertUbicationInFirestore(
        ubication: Ubication,
        showMessage: Boolean = false
    ) {
        viewModelScope.launch {
            val isSuccess = repository.insertUbication(ubication)
            _insertUbicationStatus.value = isSuccess
        }
    }

    fun getAllUbications() {
        viewModelScope.launch {
            val resultList = repository.getAllUbications()
            _allUbicationsList.value = resultList
        }
    }

}
