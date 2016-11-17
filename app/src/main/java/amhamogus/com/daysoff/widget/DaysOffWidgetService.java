package amhamogus.com.daysoff.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
    GoogleAccountCredential mCredential;
    Context mContext;
    List<CalendarListEntry> list;
    private int length = 0;
    private String TAG = "WIDGET SERVICE CLASS";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DaysOffFactory(getApplicationContext(), intent);
    }


    private class DaysOffFactory implements RemoteViewsService.RemoteViewsFactory {

        public DaysOffFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
            SharedPreferences preferences =
                    getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
            String name = preferences.getString(PREF_ACCOUNT_NAME, null);

            if (name != null) {
                mCredential = GoogleAccountCredential.usingOAuth2(
                        getApplicationContext(), Arrays.asList(SCOPES))
                        .setSelectedAccountName(name)
                        .setBackOff(new ExponentialBackOff());

                new RequestCalendarListTask(mCredential).execute();

            } else {
                Log.d(TAG, "ACCOUNT NAME NOT SET");
            }
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
            RemoteViews remoteViews =
                    new RemoteViews(mContext.getPackageName(), R.layout.row_widget_item);

            remoteViews.setTextViewText(R.id.widget_content, list.get(position).getSummary());
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
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }

    private class RequestCalendarListTask extends AsyncTask<Void, Void, List<CalendarListEntry>> {
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

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
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
            } else {
                Log.d(TAG, output.toString());
                length = output.size();
                list = output;
            }
        }
    }
}
