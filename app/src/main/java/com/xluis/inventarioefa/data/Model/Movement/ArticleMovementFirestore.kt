package com.xluis.inventarioefa.data.Model.Movement

import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ArticleMovementFirestore(
    var id: String? = null,
    val articleId: String = "",
    val articleName : String = "",
    val zoneName : String = "",
    val userId: String = "",
    val count: Int = 0,
    val actionType : MovementAction = MovementAction.TAKE,
    val date: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
) {
    init {
        if (id == null && articleId.isNotBlank() && userId.isNotBlank()) {
            id = "${userId}-${articleId}-${date}"
        }
    }
}
