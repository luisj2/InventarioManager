package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements

import com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements.ArticleZoneMovementRoomRepository
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class InsertMovementsListRoom(
    private val movementRepository: ArticleZoneMovementRoomRepository
) {
    suspend operator fun invoke(
        movementList : List<ArticleMovementsEntity>
    ) : SuspendResult<Boolean>{
        return movementRepository.insertMovements(movementList)
    }
}