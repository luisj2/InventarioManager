package com.xluis.inventarioefa._domain.model.User

import java.util.Date


data class ZoneRequest(
    val id : String = "",
    val zoneId: String = "",
    val requesterId: String = "",
    val receiverId : String = "",
    val createdAt: Date = Date()
)
