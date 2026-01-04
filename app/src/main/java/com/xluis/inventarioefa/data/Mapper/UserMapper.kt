package com.xluis.inventarioefa.data.Mapper

import com.xluis.inventarioefa._domain.model.DataClass.User.User
import com.xluis.inventarioefa.data.Model.User.UserFirestore

fun UserFirestore.toDomain() = User(
    id = this.uid,
    email = email,
    userName = userName,
    zonesIdList = zonesIdList,
)

fun User.toFirestore() = UserFirestore(
    uid = this.id,
    email = email,
    userName = userName,
    zonesIdList = zonesIdList
)