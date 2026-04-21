    package com.xluis.inventarioefa.data.Database.Room

    import androidx.room.withTransaction
    import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.withContext

    abstract class BaseRoomRepository {

        protected suspend fun <T> executeRoomOperation(block: suspend () -> T): SuspendResult<T> {
            return withContext(Dispatchers.IO) {
                try {
                    SuspendResult.Success(block())
                } catch (e: Exception) {
                    SuspendResult.Error(RoomExceptionHandler.handle(e))
                }
            }
        }

        /**
         * Ejecuta una operación transaccional (varias operaciones en una sola transacción).
         * Ideal para inserciones múltiples, updates, deletes, etc.
         */
        protected suspend fun <T> executeRoomTransaction(
            database: androidx.room.RoomDatabase,
            block: suspend () -> T
        ): SuspendResult<T> {
            return executeRoomOperation {
                database.withTransaction {
                    block()
                }
            }
        }
    }
