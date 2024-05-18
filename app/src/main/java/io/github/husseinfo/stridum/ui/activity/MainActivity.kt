package io.github.husseinfo.stridum.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.husseinfo.stridum.service.SensorListener
import io.github.husseinfo.stridum.data.StepModel
import io.github.husseinfo.stridum.data.StepRepository
import io.github.husseinfo.stridum.data.formatCount
import io.github.husseinfo.stridum.data.getDayLabel
import io.github.husseinfo.stridum.ui.theme.StridumTheme
import io.github.husseinfo.stridum.ui.theme.Typography
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

        val previousDaysCoroutine = lifecycleScope.async(Dispatchers.IO) {
            StepRepository.getPreviousDays(baseContext)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val count = countCoroutine.await()

            val previousDays = previousDaysCoroutine.await()

            setContent {
                StridumTheme {
                    Column {
                        TodayCount(
                            count = count ?: 0
                        )
                        ListPreviousDays(previousDays)
                    }
                }
            }
        }
    }
}


@Composable
fun TodayCount(count: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = Typography.titleLarge,
            text = formatCount(count, false),
            modifier = modifier
        )
        Text(
            style = Typography.titleMedium,
            text = if (count > 0) " \uD83D\uDC63 steps so far!" else "\uD83D\uDCA4 no steps yet!",
            modifier = modifier
        )
    }
}

@Composable
fun ListPreviousDays(previousDays: List<StepModel>) {
    LazyColumn(modifier = Modifier.padding(top = 40.dp, start = 20.dp, end = 20.dp)) {
        items(previousDays.size) { item ->
            Row {
                Text(
                    style = Typography.bodyMedium,
                    text = getDayLabel(item.inc()),
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    style = Typography.bodyLarge,
                    text = formatCount(previousDays[item].count, true),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatPreview() {
    val d = Calendar.getInstance().time
    val list = List(6) { StepModel(d, (3000..18000).random()) }
    StridumTheme {
        Column {
            TodayCount(
                count = 12430
            )
            ListPreviousDays(list)
        }
    }
}
