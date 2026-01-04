package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetZoneListByIdList(
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(idList: List<String>): SuspendResult<List<Zone>> {

        return when (val result = zoneFirestoreRepository.getZoneListByIdList(idList)) {
            is SuspendResult.Success -> SuspendResult.Success(result.data.map { it.toDomain() })
            is SuspendResult.Error -> SuspendResult.Error(result.message)
            else -> SuspendResult.Error("Error inesperado al obtener zonas")
        }
    }
}
