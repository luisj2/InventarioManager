package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest

import com.xluis.inventarioefa._domain.model.User.ZoneRequest
import com.xluis.inventarioefa._domain.util.map
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.User.UserZonesRequestsRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.data.Model.Firestore.User.ZoneRequestFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserRequests(
    private val requestsRepository: UserZonesRequestsRepository
) {
    suspend operator fun invoke(userId: String): SuspendResult<List<ZoneRequest>> {
        val result: SuspendResult<List<ZoneRequestFirestore>> =
            requestsRepository.getUserRequests(userId)

        return result.map { list ->
            list.orEmpty().map { dto ->
                    dto.toDomain()
                }
            }
        }
}
