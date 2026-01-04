package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest

import com.xluis.inventarioefa.data.Database.Firebase.Firestore.User.UserZonesRequestsRepository
import com.xluis.inventarioefa.data.Model.User.ZoneRequestFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserRequests(
    private val requestsRepository: UserZonesRequestsRepository
) {
    suspend operator fun invoke(userId : String) : SuspendResult<List<ZoneRequestFirestore>> {
        return requestsRepository.getUserRequests(userId)
    }
}