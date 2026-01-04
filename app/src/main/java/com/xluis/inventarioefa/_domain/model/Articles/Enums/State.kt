package com.xluis.inventarioefa.domain.model.DataClass.Articles.Enums

enum class State(val displayName: String) {
    NEW("Nuevo"),
    USED("Usado"),
    IN_REPAIR("En Reparación"),
    OBSOLETE("Obsoleto");
}