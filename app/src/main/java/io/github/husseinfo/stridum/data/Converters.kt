package io.github.husseinfo.stridum.data

import android.icu.util.Calendar
import androidx.room.TypeConverter

import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let {
            val cal = Calendar.getInstance()
            cal.time = Date(it)
            cal.resetToHour()
            cal.time
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.let {
            val cal = Calendar.getInstance()
            cal.time = it
            cal.resetToHour()
            cal.time.time
        }
    }
}
