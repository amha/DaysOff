package amhamogus.com.daysoff.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import amhamogus.com.daysoff.R;

/**
 * Implementation of App Widget functionality.
 */
public class DaysOffWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        int length = appWidgetIds.length;

        for (int i = 0; i < length; ++i) {

            Intent intent = new Intent(context, DaysOffWidgetService.class)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.days_off_widget);
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
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

