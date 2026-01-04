package com.xluis.inventarioefa.data.Model.User

data class UserFirestore(
    val uid : String = "",
    val email : String = "",
    val userName : String = "",
    val zonesIdList : List<String> = emptyList()
)