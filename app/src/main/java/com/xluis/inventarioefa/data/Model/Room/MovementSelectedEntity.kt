package com.xluis.inventarioefa.data.Model.Room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction

@Entity(
    tableName = "MovementSelected",
    indices = [
        Index(value = ["screenId", "articleId", "zoneId"], unique = true)
    ]
)
data class MovementSelectedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val screenId: String,
    val articleId: String,
    val zoneId: String,
    val zoneName: String = "",
    val articleName: String = "",
    val userId: String = "",
    val userName : String = "",
    val count: Int,
    val actionType: String = MovementAction.TAKE.displayName,
    val date: Long = System.currentTimeMillis()
)