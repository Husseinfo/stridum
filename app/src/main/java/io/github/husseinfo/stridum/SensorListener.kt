package io.github.husseinfo.stridum

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.Calendar
import android.os.Build
import android.os.IBinder
import android.util.Log
import io.github.husseinfo.stridum.data.AppDatabase
import io.github.husseinfo.stridum.data.StepModel
import io.github.husseinfo.stridum.data.StepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SensorListener : Service(), SensorEventListener {
    private val serviceScope = CoroutineScope(Job())
    private lateinit var NOTIFICATION_CHANNEL_ID: String
    private val MAX_BUFFER_SIZE = 1
    private var sinceBoot = 0
    private var buffer = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        NOTIFICATION_CHANNEL_ID = getString(R.string.notif_step_channel_id)

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                1,
                getNotification(this),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(
                1,
                getNotification(this),
            )
        }


        val sm = getSystemService(SENSOR_SERVICE) as SensorManager

        sm.registerListener(
            this,
            sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
            SensorManager.SENSOR_DELAY_NORMAL,
            60000000
        )

        return START_STICKY
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(NOTIFICATION_CHANNEL_ID, "Accuracy Changed: $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent) {
        var stepCount = event.values[0].toInt()
        if (sinceBoot == 0) {
            sinceBoot = stepCount
            return
        }

        buffer += stepCount - sinceBoot
        sinceBoot = stepCount


        if (buffer < MAX_BUFFER_SIZE)
            return
        else {
            stepCount = buffer
            buffer = 0
        }

        serviceScope.launch {
            StepRepository.updateHour(baseContext, Calendar.getInstance(), stepCount)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            val sm = getSystemService(SENSOR_SERVICE) as SensorManager
            sm.unregisterListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getNotificationBuilder(context: Context): Notification.Builder {
        val manager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_MIN
            )
        channel.importance = NotificationManager.IMPORTANCE_MIN
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.setBypassDnd(false)
        channel.setSound(null, null)
        manager.createNotificationChannel(channel)
        return Notification.Builder(
            context,
            NOTIFICATION_CHANNEL_ID
        )
    }

    fun getNotification(context: Context): Notification {
        val notificationBuilder: Notification.Builder = getNotificationBuilder(context)

        notificationBuilder.setShowWhen(true).setContentIntent(
            PendingIntent
                .getActivity(
                    context, 0, Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
        )
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setOngoing(true)
            .setContentText(context.getString(R.string.notif_step_content))
            .setContentTitle(context.getString(R.string.notif_step_title))
        return notificationBuilder.build()
    }
}
