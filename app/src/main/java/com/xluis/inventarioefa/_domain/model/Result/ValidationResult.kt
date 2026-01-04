package com.xluis.inventarioefa._domain.model.DataClass.Result

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message : String) : ValidationResult()
}