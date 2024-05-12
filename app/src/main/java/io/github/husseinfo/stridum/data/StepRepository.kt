package io.github.husseinfo.stridum.data

import android.content.Context
import android.icu.util.Calendar

class StepRepository {
    companion object {
        private var dao: StepDAO? = null

        private fun getStepDAO(context: Context): StepDAO? {
            if (dao == null)
                dao = AppDatabase.getDb(context)?.stepDAO()
            return dao
        }

        fun getStepsByDay(context: Context, cal: Calendar): Int? {
            cal.set(Calendar.HOUR, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return getStepDAO(context)?.getStepsByDay(cal.time)
        }

        fun getTodaySteps(context: Context): Int? {
            return getStepsByDay(context, Calendar.getInstance())
        }
    }
}
