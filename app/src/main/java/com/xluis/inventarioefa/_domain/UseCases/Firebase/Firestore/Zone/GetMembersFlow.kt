package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository

class GetMembersFlow(
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {
    operator fun invoke(zoneId: String) = zoneFirestoreRepository.getMembersFlow(zoneId)
}
