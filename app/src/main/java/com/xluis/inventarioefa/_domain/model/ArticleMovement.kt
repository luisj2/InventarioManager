package com.xluis.inventarioefa._domain.model.DataClass

import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import java.time.LocalDateTime
import java.util.UUID

data class ArticleMovement(
    val id: String = UUID.randomUUID().toString(),
    val articleId: String = "",
    val zoneId : String = "",
    val zoneName : String = "",
    val articleName: String = "",
    val userId: String = "",
    val userName : String = "",
    val count: Int = 0,
    val actionType: MovementAction = MovementAction.TAKE,
    val date: LocalDateTime = LocalDateTime.now()
)