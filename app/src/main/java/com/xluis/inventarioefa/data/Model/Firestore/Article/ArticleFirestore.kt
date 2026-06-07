package com.xluis.inventarioefa.data.Model.Firestore.Article

import java.util.UUID

data class ArticleFirestore(
    var id : String? = null,
    val name : String = "",
    val category : String = "",
    val zoneId : String? = null,
    var count : Int = 1,
    val descriptions : List<String> = emptyList()
){
    init {
        if(id == null && name.isNotBlank()){
            val cleanName = name.uppercase().replace(" ", "_").replace(Regex("[^A-Z0-9_]"), "")
            val shortId = UUID.randomUUID().toString().take(5)
            id = "Firestore-$cleanName-$shortId"
        }
    }
}
