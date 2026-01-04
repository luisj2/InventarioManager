package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa.data.Database.Firestore.User.UserZonesRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserZonesIds(
    private val userZonesRepository: UserZonesRepository
) {
    suspend operator fun invoke(userId: String): SuspendResult<List<String>> {
        return when (val result = userZonesRepository.getZonesIdList(userId)) {
            is SuspendResult.Success -> SuspendResult.Success(result.data)
            is SuspendResult.Error -> SuspendResult.Error(result.message)
            else -> SuspendResult.Error("Error inesperado")
        }
    }
}
