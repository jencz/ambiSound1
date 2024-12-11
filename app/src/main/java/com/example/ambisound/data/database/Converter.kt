package com.example.ambisound.data.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromLongToDate(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun fromDateToLong(value: Date): Long {
        return value.time
    }
}