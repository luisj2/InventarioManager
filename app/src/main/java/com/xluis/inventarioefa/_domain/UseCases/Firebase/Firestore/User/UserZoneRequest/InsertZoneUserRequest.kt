package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.User.UserZonesRequestsRepository
import com.xluis.inventarioefa.data.Model.User.ZoneRequestFirestore

class InsertZoneUserRequest(
    private val requestsRepository: UserZonesRequestsRepository
) {

    suspend operator fun invoke(userId : String,newRequest : ZoneRequestFirestore) : ValidationResult{
        return requestsRepository.insertUserRequest(userId,newRequest).toValidationResult("No se ha podido mandar la solicitud")
    }
}