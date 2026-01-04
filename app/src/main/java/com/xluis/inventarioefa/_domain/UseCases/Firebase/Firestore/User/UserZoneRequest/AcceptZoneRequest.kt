package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository

class AcceptZoneRequest(
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {
    suspend operator fun invoke(zoneIdToAdd: String, newMemberId: String): ValidationResult {
        return zoneFirestoreRepository.addMember(zoneIdToAdd,newMemberId).toValidationResult("No se ha podido aceptar la solicitud")
    }
}