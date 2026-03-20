package com.xluis.inventarioefa._domain.model.User

import java.util.Date


data class ZoneRequest(
    val id : String = "",
    val zoneId: String = "",
    val zoneName : String = "",
    val requesterId: String = "",
    val requesterName : String = "",
    val receiverId : String = "",
    val receiverName : String = "",
    val createdAt: Date = Date()
)
