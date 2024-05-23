package io.github.husseinfo.stridum.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
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
import io.github.husseinfo.stridum.data.StepRepository
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

    private fun updateUI() {
        val countCoroutine = lifecycleScope.async(Dispatchers.IO) {
            StepRepository.getTodaySteps(baseContext)
        }

        val previousDaysCoroutine = lifecycleScope.async(Dispatchers.IO) {
            StepRepository.getPreviousDays(baseContext)
        }

        val todayHoursCoroutine = lifecycleScope.async(Dispatchers.IO) {
            StepRepository.getTodayHours(baseContext)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val count = countCoroutine.await()
            val todayHours = todayHoursCoroutine.await()
            val previousDays = previousDaysCoroutine.await()

            setContent {
                StridumTheme {
                    Column {
                        TodayCount(count = count ?: 0)
                        if (todayHours?.size!! > 1)
                            TodayHours(todayHours)
                        else
                            Box(Modifier.padding(30.dp))
                        ListPreviousDays(previousDays)
                    }
                }
            }
        }
    }
}
