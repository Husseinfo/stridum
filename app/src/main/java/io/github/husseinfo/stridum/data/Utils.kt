package io.github.husseinfo.stridum.data

import android.icu.util.Calendar


fun Calendar.resetToHour() {
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
}

fun Calendar.resetToDay() {
    this.resetToHour()
    this.set(Calendar.HOUR, 0)
}
