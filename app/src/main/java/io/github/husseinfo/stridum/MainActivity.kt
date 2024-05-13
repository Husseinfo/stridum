package io.github.husseinfo.stridum

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.husseinfo.stridum.data.StepRepository
import io.github.husseinfo.stridum.ui.theme.StridumTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateUI()

        val requestPermissionLauncher =
            registerForActivityResult(
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
        lifecycleScope.launch(Dispatchers.Main) {
            val countCoroutineRes = countCoroutine.await()
            var count = countCoroutineRes.toString()
            if (countCoroutineRes == 0) {
                count = "No"
            }

            setContent {
                StridumTheme {
                    TodayCount(
                        count = count,
                        modifier = Modifier.padding(PaddingValues(Dp(50f)))
                    )
                }
            }
        }
    }
}


@Composable
fun TodayCount(count: String, modifier: Modifier = Modifier) {
    Text(
        text = "$count steps so far!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StridumTheme {
        TodayCount("2390")
    }
}
