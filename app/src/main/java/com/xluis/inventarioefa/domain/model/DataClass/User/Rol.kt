package com.xluis.inventarioefa.domain.model.DataClass.User

enum class Rol(
    val displayName : String
) {
    TEACHER("Profesor"),
    MAINTENANCE("Mantenimiento"),
    SECRETARY("Secretario/a"),
    JANITOR("Conserje"),
    ADMIN("Administrador");
}