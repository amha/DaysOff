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

    static final String EXTRA_ITEM = "amhamogus.com.daysoff.widget.EXTRA_ITEM";
    static final String LAUNCH = "amhamogus.com.daysoff.widget.LAUNCH";
    static final String LAUNCH_MAIN = "amhamogus.com.daysoff.widget.LAUNCH_MAIN";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_CALENDAR_NAME = "calendarName";
    private static final String PREF_CALENDAR_ID = "calendarId";
    String TAG = "WIDGET PROVIDER";
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, DaysOffWidgetService.class)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Bind intent with layout resources
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.days_off_widget);
            remoteViews.setRemoteAdapter(R.id.widget_list_view, intent);
            remoteViews.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view);

            // Define intent that will launch a calendar view
            // of the app
            Intent actionIntent = new Intent(context, DaysOffWidget.class);
            actionIntent.setAction(LAUNCH);
            actionIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            actionIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            PendingIntent listItemPendingIntent = PendingIntent
                    .getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list_view, listItemPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        Log.d(TAG, "ACTION = " + action);
        switch (action) {
            case LAUNCH:
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
                break;
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}