package io.github.husseinfo.stridum.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StepDAO {
    @get:Query("SELECT * FROM step ORDER BY date DESC")
    val all: List<StepModel>?

    @Query("SELECT * FROM step WHERE id = :id LIMIT 1")
    fun findById(id: Int): StepModel?

    @Insert
    fun insertAll(vararg counts: StepModel)

    @Update
    fun update(model: StepModel)
}
