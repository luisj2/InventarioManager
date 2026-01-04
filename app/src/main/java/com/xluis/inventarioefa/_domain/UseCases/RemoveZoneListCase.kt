package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class RemoveZoneListCase(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(zoneList: List<Zone>): ValidationResult {

        for (zone in zoneList) {

            when (zone.storageType) {

                StorageType.LOCAL -> {
                    val idLong = zone.id?.toLongOrNull()
                        ?: return ValidationResult.Error("ID inválido para zona local")

                    when (val result = zoneRoomRepository.removeZone(idLong)) {
                        is SuspendResult.Success -> {
                            if (!result.data)
                                return ValidationResult.Error("No se pudo borrar la zona local con id $idLong")
                        }
                        is SuspendResult.Error -> {
                            return ValidationResult.Error(result.message)
                        }
                        else -> return ValidationResult.Error("Error inesperado")
                    }
                }

                StorageType.FIREBASE -> {
                    val id = zone.id
                        ?: return ValidationResult.Error("ID inválido para zona Firebase")

                    when (val result = zoneFirestoreRepository.deleteZoneById(id)) {
                        is SuspendResult.Success -> {
                            if (!result.data)
                                return ValidationResult.Error("No se pudo borrar la zona Firebase con id $id")
                        }
                        is SuspendResult.Error -> {
                            return ValidationResult.Error(result.message)
                        }
                        else -> return ValidationResult.Error("Error inesperado")
                    }
                }
            }
        }

        return ValidationResult.Success
    }
}
