package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserZonesSummary(
    private val zoneRepository: ZoneFirestoreRepository,
) {

    suspend operator fun invoke(userId: String): SuspendResult<List<ZoneSummary>> {
        return zoneRepository.getUserZonesSummary(userId)
    }
}