package com.xluis.inventarioefa._domain.UseCases.Room.MovementSelected

import com.xluis.inventarioefa.data.Database.Room.MovementSelected.MovementSelectedRepository
import com.xluis.inventarioefa.data.Model.Room.MovementSelectedEntity
import kotlinx.coroutines.flow.Flow

class GetMovementsByScreenAndZoneIds(
    private val repo : MovementSelectedRepository
) {

    operator fun invoke(
        screenId : String,
        zoneId : String
    ) : Flow<List<MovementSelectedEntity>> {
        return repo.getMovementsByScreenAndZoneIds(screenId, zoneId)
    }
}