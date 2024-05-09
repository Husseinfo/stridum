package io.github.husseinfo.stridum.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [StepModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepDAO(): StepDAO?

    companion object {
        private var db: AppDatabase? = null
        fun getDb(context: Context): AppDatabase? {
            if (db == null)
                db = Room.databaseBuilder(context, AppDatabase::class.java, "db.sqlite")
                    .build()
            return db
        }
    }
}
