package io.github.husseinfo.stridum.data

import android.icu.util.Calendar
import java.time.DayOfWeek


fun Calendar.resetToHour() {
    this.set(Calendar.MINUTE, 0)
    this.set(Calendar.SECOND, 0)
    this.set(Calendar.MILLISECOND, 0)
}

fun Calendar.resetToDay() {
    this.resetToHour()
    this.set(Calendar.HOUR, 0)
}

fun formatCount(number: Int, space: Boolean): String {
    return number.toString().replace(Regex("(\\d)(?=(\\d{3})+\$)"), "\$1," + if (space) " " else "")
}


fun formatCountWidget(number: Int, space: Boolean): String {
    return formatCount(number, space) + " \uD83D\uDC63"
}

fun getDayLabel(i: Int): String {
    if (i == 1)
        return "Yesterday"

    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, (i * -1) - 1)
    return DayOfWeek.of(cal.get(Calendar.DAY_OF_WEEK)).toString().lowercase()
        .replaceFirstChar { it.uppercase() }
}
