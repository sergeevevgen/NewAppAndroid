package com.example.android.roomwordssample;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    public static String ACTION_WIDGET_RECEIVER = "android.appwidget.action.APPWIDGET_UPDATE";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = "Количество записей в БД: " + JSONHelper.getCount(context);
        // Construct the RemoteViews object

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);

        views.setTextViewText(R.id.appwidget_text, widgetText);

        Intent intent = new Intent(context, NewAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setPendingIntentTemplate(R.id.appwidget_text, pendingIntent);
        context.sendBroadcast(intent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}