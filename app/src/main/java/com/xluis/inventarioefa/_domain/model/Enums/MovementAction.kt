package com.xluis.inventarioefa.domain.model.DataClass.Enums

enum class MovementAction(val displayName: String) {
    TAKE("Retirar"),
    ADD("Añadir");
    companion object {
        fun fromName(name: String): MovementAction? {
            return entries.find { it.displayName == name }
        }
    }
}

