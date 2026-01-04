package com.xluis.inventarioefa.domain.model.DataClass.Zone

enum class StorageType {
    LOCAL,
    FIREBASE;
    companion object {
        fun fromName(name: String?): StorageType {
            return try {
                name?.let { valueOf(it.uppercase()) } ?: StorageType.LOCAL
            } catch (e: IllegalArgumentException) {
                StorageType.LOCAL
            }
        }
    }
}