package com.xluis.inventarioefa._domain.UseCases.Room.Zone

import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class SaveRoomChanges(
    private val zoneRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(
        zoneId: Long,
        articlesToSave: List<ArticleZoneEntity>,
        movementsToSave: List<ArticleMovementsEntity>
    ): SuspendResult<Boolean> {

        val articles = articlesToSave.map { it.copy(zoneId = zoneId) }
        val movements = movementsToSave.map { it.copy(zoneId = zoneId) }

        return zoneRepository.upsertArticlesAndMovements(zoneId,articles, movements)
    }
}
