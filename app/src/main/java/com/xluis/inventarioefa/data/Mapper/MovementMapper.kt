package com.xluis.inventarioefa.data.Mapper

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Model.Firestore.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.data.Model.Room.MovementSelectedEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val movementFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

// ----------------------
// Firestore → Domain
// ----------------------
fun ArticleMovementFirestore.toDomain(): ArticleMovement {
    return ArticleMovement(
        id = this.id ?: "",
        articleId = this.articleId,
        articleName = this.articleName,
        userId = this.userId,
        zoneName = this.zoneName,
        count = this.count,
        actionType = this.actionType,
        date = LocalDateTime.parse(this.date, movementFormatter)
    )
}

// ----------------------
// Domain → Firestore
// ----------------------
fun ArticleMovement.toFirestore(): ArticleMovementFirestore {
    return ArticleMovementFirestore(
        id = this.id.ifBlank { null },
        articleId = this.articleId,
        articleName = this.articleName,
        zoneName = this.zoneName,
        userId = this.userId,
        count = this.count,
        actionType = this.actionType,
        date = this.date.format(movementFormatter)
    )
}

// Conversión de ArticleMovement a ArticleMovementsEntity
fun ArticleMovement.toEntity(): ArticleMovementsEntity {
    val zoneIdLong = zoneId.toLongOrNull() ?: 0L
    return ArticleMovementsEntity(
        articleId = articleId.toLongOrNull() ?: 0,
        articleName = articleName,
        zoneId = zoneIdLong,
        count = count,
        zoneName = this.zoneName,
        actionType = actionType.name, // guardamos enum como String
        date = date
    )
}

// Conversión de ArticleMovementsEntity a ArticleMovement
fun ArticleMovementsEntity.toDomain(): ArticleMovement {
    val movementAction = try {
        MovementAction.valueOf(actionType)
    } catch (e: IllegalArgumentException) {
        MovementAction.TAKE // valor por defecto si no coincide
    }

    return ArticleMovement(
        id = id.toString(),
        articleId = articleId.toString(),
        zoneId = zoneId.toString(),
        articleName = articleName,
        zoneName = this.zoneName,
        count = count,
        actionType = movementAction,
        date = date
    )
}


fun MovementSelectedEntity.toDomain(): ArticleMovement {
    return ArticleMovement(
        id = this.id.toString(),
        articleId = this.articleId,
        zoneId = this.zoneId?.toString() ?: "",
        zoneName = this.zoneName,
        articleName = this.articleName,
        userId = this.userId,
        count = this.count,
        actionType = MovementAction.valueOf(this.actionType),
        date = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.date), ZoneOffset.UTC)
    )
}

fun ArticleMovement.toMovementSelectedEntity(screenId: String): MovementSelectedEntity {
    return MovementSelectedEntity(
        id = this.id.toLongOrNull() ?: 0,
        screenId = screenId,
        articleId = this.articleId,
        zoneId = this.zoneId,
        zoneName = this.zoneName,
        articleName = this.articleName,
        userId = this.userId,
        userName = this.userName,
        count = this.count,
        actionType = this.actionType.name,
        date = this.date.toInstant(ZoneOffset.UTC).toEpochMilli()
    )
}


