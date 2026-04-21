package com.xluis.inventarioefa.data.Database.Firebase.Auth

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseAuthRepository {
    protected suspend fun <T> executeAuthOperation(block: suspend () -> T): SuspendResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                SuspendResult.Success(block())
            } catch (e: Exception) {
                SuspendResult.Error(AuthExceptionHandler.handle(e))
            }
        }
    }
}