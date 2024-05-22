package io.github.husseinfo.stridum.ui.activity

import android.icu.util.Calendar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.husseinfo.stridum.data.StepModel
import io.github.husseinfo.stridum.data.formatCount
import io.github.husseinfo.stridum.data.formatHour
import io.github.husseinfo.stridum.data.getDayLabel
import io.github.husseinfo.stridum.ui.theme.Pink40
import io.github.husseinfo.stridum.ui.theme.Pink80
import io.github.husseinfo.stridum.ui.theme.PurpleGrey40
import io.github.husseinfo.stridum.ui.theme.PurpleGrey80
import io.github.husseinfo.stridum.ui.theme.StridumTheme
import io.github.husseinfo.stridum.ui.theme.Typography

@Composable
fun TodayCount(count: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier.fillMaxWidth()
        ) {
            drawCircle(
                color = PurpleGrey80,
                radius = 110.dp.toPx(),
                center = Offset(size.width / 2, size.height)
            )
        }

        Text(
            style = Typography.titleLarge,
            text = formatCount(count, false),
            modifier = modifier
        )
        Text(
            style = Typography.titleMedium,
            text = if (count > 0) " \uD83D\uDC63 steps so far!" else "\uD83D\uDCA4 no steps yet!",
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
fun TodayHours(hours: List<StepModel>) {
    Box(Modifier.padding(30.dp))
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(30.dp)
    ) {
        val data = hours.map { it.count }
        val coefficient = size.height / data.maxOrNull()!!
        val points = data.mapIndexed { index, value ->
            Offset(
                x = index * (size.width / (data.size - 1)),
                y = size.height - (value * coefficient)
            )
        }

        drawLine(
            color = PurpleGrey40,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Square
        )

        var previousPoint = points[0]
        val paintX = Paint().apply {
            color = PurpleGrey40
        }
        val paintY = Paint().apply {
            color = Pink40
        }
        points.forEachIndexed { i, point ->
            drawCircle(
                color = Pink40,
                radius = 5.dp.toPx(),
                center = point
            )

            drawCircle(
                color = Color.DarkGray,
                radius = 3.dp.toPx(),
                center = Offset(point.x, size.height)
            )

            drawLine(
                color = Pink80,
                start = previousPoint,
                end = point,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Butt
            )

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    hours[i].date.formatHour(),
                    point.x - 30,
                    size.height + 60,
                    paintX.asFrameworkPaint().apply {
                        this.textSize = 45f
                    }
                )
                canvas.nativeCanvas.drawText(
                    ((size.height - point.y) / coefficient).toInt().toString(),
                    point.x - 40,
                    point.y - 40,
                    paintY.asFrameworkPaint().apply {
                        textSize = 40f
                    }
                )
            }

            previousPoint = point
        }
    }
}

@Composable
fun ListPreviousDays(previousDays: List<StepModel>) {
    LazyColumn(modifier = Modifier.padding(top = 20.dp, start = 20.dp, end = 20.dp)) {
        items(previousDays.size) { item ->
            Row(Modifier.padding(top = 10.dp)) {
                Text(
                    style = Typography.bodyMedium,
                    text = getDayLabel(item.inc()),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    style = Typography.bodyLarge,
                    text = formatCount(previousDays[item].count, true),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    val d = Calendar.getInstance().time
    val previousDays = List(6) { StepModel(d, (3000..18000).random()) }
    val todayHours = List(5) { StepModel(d, ((it + 1) * 100)) }
    StridumTheme {
        Column {
            TodayCount(
                count = (10000..18000).random()
            )
            TodayHours(todayHours)
            ListPreviousDays(previousDays)
        }
    }
}
