package com.xluis.inventarioefa.domain.model.DataClass.Result

sealed class SuspendResult<out T> {
    data object Idle : SuspendResult<Nothing>()
    data object Loading : SuspendResult<Nothing>()
    data class Success<out T>(val data: T) : SuspendResult<T>()
    data class Error(val message: String) : SuspendResult<Nothing>()
}