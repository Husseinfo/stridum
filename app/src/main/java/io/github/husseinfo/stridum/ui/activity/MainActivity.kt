package io.github.husseinfo.stridum.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.husseinfo.stridum.data.StepModel
import io.github.husseinfo.stridum.data.StepRepository
import io.github.husseinfo.stridum.data.resetToDay
import io.github.husseinfo.stridum.service.SensorListener
import io.github.husseinfo.stridum.ui.theme.StridumTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startService(Intent(this, SensorListener::class.java))
            } else {
                finish()
            }
        }

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startService(Intent(this, SensorListener::class.java))
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI(cal: Calendar = Calendar.getInstance()) {
        val previousDaysCoroutine = lifecycleScope.async(Dispatchers.IO) {
            StepRepository.getPreviousDays(baseContext)
        }
        val dayHoursCoroutine = lifecycleScope.async(Dispatchers.IO) {
            StepRepository.getDayHours(baseContext, cal)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val dayHours = dayHoursCoroutine.await()
            val previousDays = previousDaysCoroutine.await()

            setContent {
                StridumTheme {
                    Column {
                        TodayCount(count = dayHours?.sumOf(StepModel::count) ?: 0)
                        if (dayHours?.size!! > 1)
                            TodayHours(dayHours)
                        else
                            Box(Modifier.padding(30.dp))
                        ListPreviousDays(previousDays, ::onDayClick)
                    }
                }
            }
        }
    }

    private fun onDayClick(step: StepModel) {
        Calendar.getInstance().apply {
            time = step.date
            resetToDay()
            updateUI(this)
        }
    }
}
