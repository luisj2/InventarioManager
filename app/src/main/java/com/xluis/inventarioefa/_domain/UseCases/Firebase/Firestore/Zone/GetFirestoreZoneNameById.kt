package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetFirestoreZoneNameById(
    private val zoneRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(zoneId: String): SuspendResult<String> {
        return when (val result = zoneRepository.getZoneNameById(zoneId)) {
            is SuspendResult.Success -> SuspendResult.Success(result.data)
            is SuspendResult.Error -> SuspendResult.Error(result.message)
            else -> SuspendResult.Error("Error inesperado al obtener el nombre de la zona")
        }
    }
}
