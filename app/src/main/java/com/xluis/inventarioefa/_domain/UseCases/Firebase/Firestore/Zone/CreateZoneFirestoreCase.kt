package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.flatMapRollback
import com.xluis.inventarioefa._domain.util.fold
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.User.UserZonesRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Model.Article.ArticleFirestore
import com.xluis.inventarioefa.data.Model.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.data.Model.Zone.ZoneFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateZoneFirestoreCase(
    private val userZonesRepository: UserZonesRepository,
    private val zonesFirestoreRepository: ZoneFirestoreRepository,
    private val articleRepository: ArticleZoneFirestoreRepository,
    private val movementRepository: ArticleMovementRepository
) {

    suspend operator fun invoke(
        zone: ZoneFirestore,
        articleList: List<ArticleFirestore>,
        parentId: String?,
        userId: String
    ): ValidationResult {

        val zoneId = zone.id ?: return ValidationResult.Error("Ha ocurrido un fallo al crear la zona")

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

        // 3️⃣ Encadenar operaciones con rollback centralizado
        return zonesFirestoreRepository.insertZone(zoneWithHierarchy)
            .toValidationResult { rollbackZone(zoneId) }
            .flatMapRollback({ rollbackZone(zoneId) }) {
                articleRepository.insertArticleList(zoneId, articleList)
                    .toValidationResult { rollbackZone(zoneId) }
            }
            .flatMapRollback({ rollbackZone(zoneId) }) {
                movementRepository.insertMovementsList(zoneId, movements)
                    .toValidationResult { rollbackZone(zoneId) }
            }
            .flatMapRollback({ rollbackZone(zoneId, parentId) }) {
                parentId?.let { zonesFirestoreRepository.addChildToZone(it, zoneId).toValidationResult { rollbackZone(zoneId, it) } }
                    ?: ValidationResult.Success
            }
            .flatMapRollback({ rollbackZone(zoneId, parentId) }) {
                userZonesRepository.addZoneId(zoneId, userId)
                    .toValidationResult { rollbackZone(zoneId, parentId) }
            }
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

    private suspend fun rollbackZone(zoneId: String, parentId: String? = null) {
        zonesFirestoreRepository.deleteZoneById(zoneId)
        articleRepository.deleteArticlesByZoneId(zoneId)
        movementRepository.deleteMovementsByZoneId(zoneId)
        parentId?.let { zonesFirestoreRepository.removeChildId(it, zoneId) }
    }
}
