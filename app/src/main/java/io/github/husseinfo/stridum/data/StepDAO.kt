package io.github.husseinfo.stridum.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface StepDAO {
    @get:Query("SELECT * FROM step ORDER BY date DESC")
    val all: List<StepModel>?

    @Query("SELECT * FROM step where date = :date")
    fun getCurrentHour(date: Date): StepModel


    @Query("SELECT SUM(count) FROM step where date >= :date")
    fun getTodaySteps(date: Date): Int

    @Query("SELECT SUM(count) FROM step where date BETWEEN :date and :date + 24 * 3600 * 1000")
    fun getStepsByDay(date: Date): Int

    @Query("SELECT * FROM step where date BETWEEN :date and :date + 24 * 3600 * 1000")
    fun getDayHours(date: Date): List<StepModel>

    @Query("SELECT * FROM step where date >= :date")
    fun getTodayHours(date: Date): List<StepModel>

    @Insert
    fun insert(model: StepModel)

    @Query("UPDATE step set count = count + :count WHERE date = :date")
    fun incrementCount(date: Date, count: Int): Int
}
