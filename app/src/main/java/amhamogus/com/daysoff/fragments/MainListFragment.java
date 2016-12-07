/*
 * Copyright 2016 Amha Mogus. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package amhamogus.com.daysoff.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.adapters.CalendarItemRecyclerViewAdapter;
import amhamogus.com.daysoff.data.DaysOffContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static final int LOADER_ID = 110;

    public Toast mOutputText;
    String TAG = "MAIN FRAGMENT";
    GoogleAccountCredential mCredential;
    @BindView(R.id.list)
    RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    private List<CalendarListEntry> returnedCalendarList;

    public MainListFragment() {
    }

    @SuppressWarnings("unused")
    public static MainListFragment newInstance(int columnCount) {
        return new MainListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref =
                getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String name = pref.getString(PREF_ACCOUNT_NAME, null);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(name)
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.list_calendar, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState == null) {
            recyclerView.setVisibility(View.GONE);

            // request data from cursor
            Cursor data = getContext().getContentResolver().query(
                    DaysOffContract.DaysOffCalendarsEntry.CONTENT_URI,
                    new String[]{DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_KEY, DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_SUMMARY},
                    null,
                    null,
                    null);
            data.moveToFirst();

            if (data.getCount() < 1) {
                data.close();
                // Running for the first time, call server,
                // get calendar list, add it to the content provider
                // and then
                new RequestCalendarListTask(mCredential).execute();
            } else {
                loaderHelper();
            }
        } else {
            Log.d(TAG, "saved instance not null");
            loaderHelper();
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Check that the activity implements the interaction handler
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                DaysOffContract.DaysOffCalendarsEntry.CONTENT_URI,
                new String[]{DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_KEY, DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_SUMMARY},
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int idColumnIndex =
                    data.getColumnIndex(DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_KEY);
            int summaryColumnIndex =
                    data.getColumnIndex(DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_SUMMARY);

            // Convery cursor data into a list of calendar
            // item objects.
            CalendarListEntry entry;
            List<CalendarListEntry> mList = new ArrayList<>(data.getCount());

            for (int i = 0; i < data.getCount(); i++) {
                entry = new CalendarListEntry();
                entry.setSummary(data.getString(summaryColumnIndex));
                entry.setId(data.getString(idColumnIndex));
                mList.add(entry);
                data.moveToNext();
            }

            // Update Recycler View with data retrieved from
            // the content provider.
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(
                    new CalendarItemRecyclerViewAdapter(mList, mListener));
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "LOADER IS NULL");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loaderHelper() {
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onCalendarSelectedInteraction(String item, String name);
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
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
            } catch (UserRecoverableAuthIOException e) {
                Log.d(TAG, "USER RECOVERABLE EXCEPTION THROWN");
                startActivityForResult(e.getIntent(), 1001);
                return null;
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
                mOutputText.makeText(getActivity().getApplicationContext(),
                        "No results returned.", Toast.LENGTH_SHORT).show();
            } else {
                Vector<ContentValues> cVVector = new Vector<ContentValues>(output.size());
                for (CalendarListEntry listEntry : output) {
                    ContentValues dbValues = new ContentValues();
                    dbValues.put(DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_IDENTIFIER, listEntry.getId());
                    dbValues.put(DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_SUMMARY, listEntry.getSummary());
                    dbValues.put(DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_KIND, listEntry.getKind());
                    dbValues.put(DaysOffContract.DaysOffCalendarsEntry.COLUMN_CAL_KEY, listEntry.getId());
                    cVVector.add(dbValues);
                }

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver()
                            .bulkInsert(DaysOffContract.DaysOffCalendarsEntry.CONTENT_URI, cvArray);
                }
                loaderHelper();
            }
        }
    }
}
