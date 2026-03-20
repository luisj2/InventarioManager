package com.xluis.inventarioefa._domain.model.Enums

enum class DateMode(val displayName: String) {
    ASCENDING("Ascendente ↑"),
    DESCENDING("Descendente ↓");

    companion object {
        /**
         * Devuelve el enum correspondiente al displayName.
         * Retorna null si no coincide con ninguna opción.
         */
        fun fromName(name: String): DateMode? {
            return entries.find { it.displayName == name }
        }

        /**
         * Cambia al modo opuesto.
         */
        fun toggle(current: DateMode): DateMode {
            return when (current) {
                ASCENDING -> DESCENDING
                DESCENDING -> ASCENDING
            }
        }
    }
}
