package com.xluis.inventarioefa.data.Database.Room.Converters

import androidx.room.TypeConverter
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

    @TypeConverter
    fun fromList(value: List<String>?): String? =
        value?.joinToString(",")

    @TypeConverter
    fun toList(value: String?): List<String>? =
        value?.split(",")

    @TypeConverter
    fun fromLongList(value: List<Long>?): String? =
        value?.joinToString(",")

    @TypeConverter
    fun toLongList(value: String?): List<Long> =
        value?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
}