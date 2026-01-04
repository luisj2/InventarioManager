package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.User.UserZonesRequestsRepository

class DeleteUserZoneRequest(
    private val requestsRepository: UserZonesRequestsRepository
) {
    suspend operator fun invoke(userId : String,requestId : String) : ValidationResult{
        return requestsRepository.deleteUserRequest(userId,requestId).toValidationResult("No se ha podido eliminar la solicitud")
    }
}