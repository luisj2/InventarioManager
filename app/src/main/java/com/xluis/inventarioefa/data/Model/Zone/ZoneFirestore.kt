package com.xluis.inventarioefa.data.Model.Zone

import java.util.UUID

data class ZoneFirestore (
    var id: String? = null,
    val name: String = "",
    val childIdList: List<String>? = null,
    val parentIdList : List<String>? = null,
    val ownerId : String? = null,
    val membersId : List<String>? = null
) {
    init {
        if (id == null && name.isNotBlank()) {
            val cleanName = name.uppercase()
                .replace(" ", "_")
                .replace(Regex("[^A-Z0-9_]"), "")
            val shortId = UUID.randomUUID().toString().take(5)
            id = "$cleanName-$shortId"
        }
    }
}
