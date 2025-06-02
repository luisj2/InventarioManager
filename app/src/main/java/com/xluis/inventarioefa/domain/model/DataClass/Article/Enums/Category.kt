package com.xluis.inventarioefa.domain.model.DataClass.Article.Enums

import com.xluis.inventarioefa.R

enum class Category (
    val displayName : String,
    val icon : Int
) {
    TECNOLOGY("Tecnología", R.drawable.category_tecnology),
    FURNITURE("Mobiliario",R.drawable.category_furniture),
    DIDACTIC_MATERIAL("Material Didáctico",R.drawable.category_didactic_material),
    OTHER("Otro",R.drawable.category_other);
    companion object {
        fun fromDisplayName(name: String): Category? {
            return entries.find { it.displayName == name }
        }
    }

}

