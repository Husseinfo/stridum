package io.github.husseinfo.stridum.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [StepModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepDAO(): StepDAO?

    companion object {
        private var db: AppDatabase? = null

        fun getDb(context: Context): AppDatabase? {
            if (db == null)
                db = Room.databaseBuilder(context, AppDatabase::class.java, "steps.sqlite")
                    .build()
            return db
        }
    }
}
