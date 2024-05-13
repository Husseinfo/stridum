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
            cal.resetToDay()
            return getStepDAO(context)?.getStepsByDay(cal.time)
        }

        fun getTodaySteps(context: Context): Int? {
            Calendar.getInstance().also {
                it.resetToDay()
                return getStepDAO(context)?.getTodaySteps(it.time)
            }
        }

        fun getTodayHours(context: Context): List<StepModel>? {
            Calendar.getInstance().also {
                it.resetToDay()
                return getStepDAO(context)?.getTodayHours(it.time)
            }
        }

        fun updateHour(context: Context, cal: Calendar, count: Int) {
            cal.resetToHour()
            if (getStepDAO(context)?.incrementCount(cal.time, count) != 1) {
                getStepDAO(context)?.insert(StepModel(date = cal.time, count = count))
            }
        }
    }
}
