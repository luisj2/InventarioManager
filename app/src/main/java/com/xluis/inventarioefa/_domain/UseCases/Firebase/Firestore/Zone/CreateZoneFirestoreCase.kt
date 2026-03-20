package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.fold
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Model.Firestore.Article.ArticleFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Zone.ZoneFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateZoneFirestoreCase(
    private val zonesFirestoreRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(
        zone: ZoneFirestore,
        articleList: List<ArticleFirestore>,
        parentId: String?,
        userId: String
    ): ValidationResult {



        // 1️⃣ Preparar zona hija con jerarquía real
        val zoneWithHierarchy = prepareZoneHierarchy(zone, parentId)
            ?: return ValidationResult.Error("El padre no existe")

        // 2️⃣ Crear lista de movimientos
        val movements = articleList.map { article ->
            ArticleMovementFirestore(
                articleId = article.id ?: "",
                articleName = article.name,
                userId = userId,
                count = article.count,
                actionType = MovementAction.ADD,
                date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
        }

        return zonesFirestoreRepository.createZone(zoneWithHierarchy,articleList,movements,parentId,userId).toValidationResult()
    }

    private suspend fun prepareZoneHierarchy(zone: ZoneFirestore, parentId: String?): ZoneFirestore? {
        return if (parentId != null) {
            val parent = zonesFirestoreRepository.getZoneById(parentId).fold(
                onSuccess = { it },
                onError = { return null }
            ) ?: return null
            zone.copy(parentIdList = (parent.parentIdList ?: emptyList()) + parentId)
        } else zone
    }

}
