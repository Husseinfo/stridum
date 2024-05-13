package io.github.husseinfo.stridum.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "step")
class StepModel(
    @PrimaryKey
    @field:ColumnInfo(name = "date")
    var date: Date,

    @field:ColumnInfo(name = "count")
    var count: Int,
)
