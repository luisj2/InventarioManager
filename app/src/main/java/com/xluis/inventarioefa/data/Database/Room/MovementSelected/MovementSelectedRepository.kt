package com.xluis.inventarioefa.data.Database.Room.MovementSelected

import com.xluis.inventarioefa.data.Database.Room.BaseRoomRepository
import com.xluis.inventarioefa.data.Model.Room.MovementSelectedEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.flow.Flow

class MovementSelectedRepository(
    private val dao: MovementSelectedDao
) : BaseRoomRepository() {

    suspend fun insertMovements(
        movements: List<MovementSelectedEntity>
    ): SuspendResult<Boolean> {

        return executeRoomOperation {
            if (movements.isEmpty()) throw Exception("No hay ningun movimiento que añadir")
            dao.insertMovementSelected(movements).isNotEmpty()
        }
    }
    suspend fun insertOrUpdateMovements(
        movements: List<MovementSelectedEntity>
    ): SuspendResult<Boolean> {

        return executeRoomOperation {
            if (movements.isEmpty()) throw Exception("No hay movimientos que añadir")

            movements.forEach { movement ->

                val existing = dao.getMovement(
                    movement.screenId,
                    movement.articleId,
                    movement.zoneId
                )

                if (existing != null) {
                    // Si existe actualizamos (ejemplo sumando cantidad)
                    val updatedMovement = existing.copy(
                        count = existing.count + movement.count
                    )

                    dao.updateMovement(updatedMovement)

                } else {
                    // Si no existe insertamos
                    dao.insertMovementSelected(listOf(movement))
                }
            }

            true
        }
    }

    suspend fun clearAllByScreenAndZone(
        screenId: String,
        zoneId: String
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            val rowsDeleted = dao.clearMovementSelectedByScreenAndZone(screenId, zoneId)
            rowsDeleted > 0
        }
    }

    fun getMovementsByScreenAndZoneIds(
        screenId : String,
        zoneId : String
    ) : Flow<List<MovementSelectedEntity>> {
        return dao.getMovementsByScreenAndZoneIds(screenId,zoneId)
    }

    suspend fun deleteByMovementIds(
        movementIds: List<String>
    ): SuspendResult<Boolean> {

        return executeRoomOperation {
            if (movementIds.isEmpty()) throw Exception("No hay ningun movimiento que eliminar")
            dao.deleteByMovementIds(movementIds) > 0
        }
    }

    suspend fun deleteMovementsByScreenAndZone(
        articleIds: List<String>,
        screenId: String,
        zoneId: String
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            if (articleIds.isEmpty()) throw Exception("No hay ningún artículo que eliminar")
            dao.deleteByMovementsIdsAndScreenAndZone(articleIds, screenId, zoneId) > 0
        }
    }

    suspend fun clearAll(): SuspendResult<Boolean> {

        return executeRoomOperation {
            dao.clearMovementSelected() > 0
        }
    }
}