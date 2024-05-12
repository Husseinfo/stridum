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

    @Query("SELECT SUM(count) FROM step where date BETWEEN :date and :date + 24 * 3600 * 1000")
    fun getStepsByDay(date: Date): Int

    @Insert
    fun insert(model: StepModel)

    @Query("UPDATE step set count = :count WHERE date = :date")
    fun updateCount(date: Date, count: Int)

    @Update
    fun update(model: StepModel)
}
