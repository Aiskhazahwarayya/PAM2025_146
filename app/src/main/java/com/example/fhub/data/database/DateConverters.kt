package com.example.fhub.data.database

import androidx.room.TypeConverter
import java.sql.Date
import java.sql.Timestamp

class DateConverters {
    // Converter untuk java.sql.Timestamp (Data Project)
    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? = value?.let { Timestamp(it) }

    @TypeConverter
    fun timestampToLong(timestamp: Timestamp?): Long? = timestamp?.time

    // Converter untuk java.sql.Date (Data Invoice)
    @TypeConverter
    fun fromSqlDate(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun sqlDateToLong(date: Date?): Long? = date?.time
}