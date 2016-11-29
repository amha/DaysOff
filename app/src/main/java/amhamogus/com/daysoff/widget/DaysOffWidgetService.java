package amhamogus.com.daysoff.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import amhamogus.com.daysoff.R;

/**
 * Updates the DaysOffWidget with remote data.
 */

public class DaysOffWidgetService extends RemoteViewsService {

    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    static int mAppWidgetId;
    static int runCount = 0;
    GoogleAccountCredential mCredential;
    String name;
    int length;
    Context mContext;
    List<CalendarListEntry> list;
    RemoteViews remoteViews;
    private String TAG = "WIDGET SERVICE CLASS";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DaysOffFactory(this.getApplicationContext(), intent);
    }

    class DaysOffFactory implements RemoteViewsService.RemoteViewsFactory {

        public DaysOffFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            name = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);

            if (name != null) {
                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                        .setSelectedAccountName(name)
                        .setBackOff(new ExponentialBackOff());
            }
            if (name != null) {
                // The user has previously setup their google user account with
                // the app. So we request their list of calendars from the server.
                new RequestCalendarListTask(mCredential).execute();
                runCount += 1;
            } else {
                // The app has not been used before. As the user to launch
                // the app and select a google account to proceed.
                Log.d(TAG, "ACCOUNT NAME NOT SET");
            }
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return length;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            // Bind calendar data with the UI element
            remoteViews =
                    new RemoteViews(mContext.getPackageName(), R.layout.row_widget_item);
            remoteViews.setTextViewText(R.id.widget_content, list.get(position).getSummary());
            // Prepare to pass data when the usr clicks on a row item
            Bundle bundle = new Bundle();
            bundle.putInt(DaysOffWidget.EXTRA_ITEM, position);
            bundle.putString("ID", list.get(position).getId());
            bundle.putString("NAME", list.get(position).getSummary());
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(bundle);
            remoteViews.setOnClickFillInIntent(R.id.widget_content, fillInIntent);
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

    class RequestCalendarListTask extends AsyncTask<Void, Void, List<CalendarListEntry>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        RequestCalendarListTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off - Debug")
                    .build();
        }

        @Override
        protected List<CalendarListEntry> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<CalendarListEntry> getDataFromApi() throws IOException {
            CalendarList mList = mService.calendarList().list().execute();
            return mList.getItems();
        }

        @Override
        protected void onPostExecute(List<CalendarListEntry> output) {
            if (output == null || output.size() == 0) {
                // Show toast when the server doesn't return anything
                Log.d(TAG, "Could not fetch calendar list.");
            } else {
                length = output.size();
                list = output;
                AppWidgetManager.getInstance(mContext)
                        .notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.widget_list_view);
            }
        }
    }
}
