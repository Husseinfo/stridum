package io.github.husseinfo.stridum

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener


class SensorListener : SensorEventListener {

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }

    override fun onSensorChanged(event: SensorEvent) {
        val stepCount = event.values[0].toInt()
    }
}
