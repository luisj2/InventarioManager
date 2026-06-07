package com.xluis.inventarioefa.data.Database.Room.Converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime (date : LocalDateTime?) : String?{
        return date?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return dateString?.let { LocalDateTime.parse(it, formatter) }
    }

    private val gson = Gson()

    @TypeConverter
    fun fromList(value: List<String>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun fromLongList(value: List<Long>?): String? =
        value?.joinToString(",")

    @TypeConverter
    fun toLongList(value: String?): List<Long> =
        value?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
}