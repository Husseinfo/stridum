package io.github.husseinfo.stridum.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "step")
class StepModel(
    @field:ColumnInfo(name = "count")
    var count: Int,

    @field:ColumnInfo(name = "date")
    var date: Date,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id = 0
}
