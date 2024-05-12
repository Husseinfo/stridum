package io.github.husseinfo.stridum.data

import androidx.room.TypeConverter
import java.util.Calendar
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let {
            val cal = Calendar.getInstance()
            cal.time = Date(it)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.time
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.let {
            val cal = Calendar.getInstance()
            cal.time = it
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.time.time
        }
    }
}
