package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository

class RemoveZoneMember(
    private val zoneRepository : ZoneFirestoreRepository
) {
    suspend operator fun invoke(zoneId : String,memberIdToDelete : String) : ValidationResult{
        return zoneRepository.removeMember(zoneId,memberIdToDelete).toValidationResult()
    }
}