package com.xluis.inventarioefa._domain.util

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult

inline fun ValidationResult.flatMap(transform: () -> ValidationResult): ValidationResult =
    when (this) {
        is ValidationResult.Success -> transform()
        is ValidationResult.Error -> this
    }

suspend fun ValidationResult.flatMapSuspend(transform: suspend () -> ValidationResult): ValidationResult =
    when (this) {
        is ValidationResult.Success -> transform()
        is ValidationResult.Error -> this
    }

fun ValidationResult.toBoolean(): Boolean = this is ValidationResult.Success

suspend fun ValidationResult.flatMapRollback(
    rollback: suspend () -> Unit,
    transform: suspend () -> ValidationResult
): ValidationResult = when (this) {
    is ValidationResult.Success -> {
        val next = transform()
        if (next is ValidationResult.Error) rollback()
        next
    }

    is ValidationResult.Error -> this
}

inline fun ValidationResult.onSuccess(action: () -> Unit): ValidationResult {
    if (this is ValidationResult.Success) action()
    return this
}

inline fun ValidationResult.onError(action: (ValidationResult.Error) -> Unit): ValidationResult {
    if (this is ValidationResult.Error) action(this)
    return this
}



