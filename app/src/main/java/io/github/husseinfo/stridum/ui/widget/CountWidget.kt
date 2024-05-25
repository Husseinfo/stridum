package io.github.husseinfo.stridum.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.husseinfo.stridum.R
import io.github.husseinfo.stridum.data.StepRepository
import io.github.husseinfo.stridum.data.formatCountWidget
import io.github.husseinfo.stridum.ui.activity.MainActivity

class CountWidget : AppWidgetProvider() {
    companion object {
        const val ACTION_UPDATE_WIDGET = "ACTION_UPDATE_WIDGET"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds =
                appWidgetManager.getAppWidgetIds(ComponentName(context, CountWidget::class.java))
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val view = RemoteViews(context.packageName, R.layout.count_widget)
    val count = StepRepository.getTodaySteps(context)
    view.setTextViewText(R.id.appwidget_text, formatCountWidget(count ?: 0, false))

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    view.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent)

    appWidgetManager.updateAppWidget(appWidgetId, view)
}
