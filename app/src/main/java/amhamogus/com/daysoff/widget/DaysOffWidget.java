package amhamogus.com.daysoff.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import amhamogus.com.daysoff.CalendarActivity;
import amhamogus.com.daysoff.R;

/**
 * Implementation of App Widget functionality.
 */
public class DaysOffWidget extends AppWidgetProvider {

    // Custom actions for the Widget
    public static final String EXTRA_ITEM = "amhamogus.com.daysoff.widget.EXTRA_ITEM";
    public static final String LAUNCH = "amhamogus.com.daysoff.widget.LAUNCH";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_CALENDAR_NAME = "calendarName";
    private static final String PREF_CALENDAR_ID = "calendarId";
    String TAG = "WIDGET PROVIDER";
    // Shared preference
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        int length = appWidgetIds.length;
        for (int i = 0; i < length; ++i) {
            Intent intent = new Intent(context, DaysOffWidgetService.class)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            // Bind intent with layout resources
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.days_off_widget);
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            // Define intent that will launch the main app
            Intent actionIntent = new Intent(context, DaysOffWidget.class);
            actionIntent.setAction(LAUNCH);
            actionIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            actionIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent toastPendingIntent = PendingIntent
                    .getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AMHA", "ON RECEIVE CALLED, action = " + intent.getAction());

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(LAUNCH)) {
            // Action that is called when the user selects an item from the
            // widget's collection view. Based on collection item data,
            // the widget launches the Calendar Activity with the user selected
            // calendar name.
            settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
            editor = settings.edit();
            editor.putString(PREF_CALENDAR_NAME, intent.getStringExtra("NAME"));
            editor.putString(PREF_CALENDAR_ID, intent.getStringExtra("ID"));
            editor.commit();

            Intent mIntent = new Intent(context, CalendarActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
        super.onReceive(context, intent);
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