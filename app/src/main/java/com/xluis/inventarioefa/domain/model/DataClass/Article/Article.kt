package com.xluis.inventarioefa.domain.model.DataClass.Article

import java.util.UUID

data class Article(
    var id : String? = null,
    val name : String = "",
    val category : String? = null,
    val ubication : String? = null,
    val count : Int = 1,
    val state : String? = null,
    val description : String = "",
){
    init {
        if(id == null && name.isNotBlank()){
            val cleanName = name.uppercase().replace(" ", "_").replace(Regex("[^A-Z0-9_]"), "")
            val shortId = UUID.randomUUID().toString().take(5)
            id = "$cleanName-$shortId"
        }
    }
}
