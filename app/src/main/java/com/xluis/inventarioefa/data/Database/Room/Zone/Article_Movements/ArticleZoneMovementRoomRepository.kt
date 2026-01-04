package com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements

import com.xluis.inventarioefa.data.Database.Room.BaseRoomRepository
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class ArticleZoneMovementRoomRepository (private val dao : ArticleMovementDao) : BaseRoomRepository() {

    suspend fun insertMovement (articleMovementsEntity: ArticleMovementsEntity) : SuspendResult<Boolean>{
        return executeRoomOperation {
            dao.insertMovement(articleMovementsEntity) > 0
        }
    }

    suspend fun insertMovements (movementList : List<ArticleMovementsEntity>) : SuspendResult<Boolean>{
        return executeRoomOperation {
            dao.insertMovements(movementList)
            true
        }
    }

    suspend fun getMovementsListByZoneId (zoneId : Long) : SuspendResult<List<ArticleMovementsEntity>>{
        return executeRoomOperation {
            dao.getMovementListByZoneId(zoneId)
        }
    }
    suspend fun deleteMovementsByIdList(idList: List<Long>, zoneId: Long): SuspendResult<Boolean> {
        return executeRoomOperation {
            dao.deleteMovementsByIdList(idList, zoneId) > 0
        }
    }
}