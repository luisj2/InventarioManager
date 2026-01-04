package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest

import com.xluis.inventarioefa.data.Database.Firestore.User.UserZonesRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserLoggedUsername(
    private val userZonesRepository: UserZonesRepository
) {

    suspend operator fun invoke(userId : String) : SuspendResult<String>{
        return userZonesRepository.getLoggedUsername(userId)
    }
}