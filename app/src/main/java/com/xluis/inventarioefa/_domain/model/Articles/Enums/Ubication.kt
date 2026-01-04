package com.xluis.inventarioefa.domain.model.DataClass.Articles.Enums

import java.util.UUID

data class Ubication(
    var id : String? = null,
    val name : String = ""
){
    init {
        if(id == null && name.isNotBlank()){
            val cleanName = name.uppercase().replace(" ", "_").replace(Regex("[^A-Z0-9_]"), "")
            val shortId = UUID.randomUUID().toString().take(5)
            id = "$cleanName-$shortId"
        }
    }
}

