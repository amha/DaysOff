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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.model.EventCollection;
import amhamogus.com.daysoff.utils.CollectionHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class CalendarFragment extends Fragment {

    private static final String TAG = "CALENDAR FRAGMENT";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_CALENDAR_NAME = "calendarName";
    private static final String PREF_CALENDAR_ID = "calendarId";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    String currentAccountName;
    String calendarName;
    String calendarId;
    GoogleAccountCredential mCredential;
    private CalendarPickerView calendar;
    private ProgressBar mProgress;
    private EventCollection eventsReturnedCollection;
    private OnCalendarSelectionListener calendarSelection;

    public CalendarFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CalendarFragment newInstance(int sectionNumber) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsReturnedCollection = new EventCollection();

        SharedPreferences preferences = getActivity()
                .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        currentAccountName = preferences.getString(PREF_ACCOUNT_NAME, null);
        calendarName = preferences.getString(PREF_CALENDAR_NAME, "primary");
        calendarId = preferences.getString(PREF_CALENDAR_ID, "primary");

        mCredential = GoogleAccountCredential
                .usingOAuth2(getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(currentAccountName)
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_calendar, container, false);
        mProgress = (ProgressBar) rootView.findViewById(R.id.calendar_progressbar);
        mProgress.setVisibility(View.VISIBLE);

        // Initialize calendar widget
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        calendar = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
        calendar.setVisibility(View.INVISIBLE);
        calendar.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.SINGLE)
                .withSelectedDate(today);

        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                passDate(date, eventsReturnedCollection);
            }

            @Override
            public void onDateUnselected(Date date) {
                calendar.setSelected(false);
            }
        });

        //Hook into calendar widget
        List<CalendarCellDecorator> decoratorList = new ArrayList<>();
        decoratorList.add(new DayDecorator());
        calendar.setDecorators(decoratorList);

        new RequestEventsTask(mCredential).execute();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Check that the activity implements the interaction handler
        if (context instanceof CalendarFragment.OnCalendarSelectionListener) {
            calendarSelection = (OnCalendarSelectionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCalendarSelectionListener");
        }
    }

    public void passDate(Date mDate, EventCollection eventCollection) {
        calendarSelection.onCalendarSelected(mDate, eventCollection);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        calendarSelection = null;
    }

    public interface OnCalendarSelectionListener {
        void onCalendarSelected(Date date, EventCollection events);
    }

    /**
     * Request a list of events for a given calendar.
     */
    class RequestEventsTask extends AsyncTask<Void, Void, List<Event>> {

        private com.google.api.services.calendar.Calendar mService = null;

        RequestEventsTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off - Debug")
                    .build();
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            try {
                return getEventsFromApi();
            } catch (Exception e) {
                cancel(true);
                return null;
            }
        }

        private List<Event> getEventsFromApi() throws IOException {
            String pageToken = null;
            Events events;
            List<Event> items;
            DateTime now = new DateTime(System.currentTimeMillis());
            do {
                events = mService.events().list(calendarId)
                        .setPageToken(pageToken)
                        .setTimeMin(now)
                        .setMaxResults(20)
                        .execute();
                items = events.getItems();
                pageToken = events.getNextPageToken();
            }
            while (pageToken != null);
            return items;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(List<Event> output) {

            if (output == null || output.size() < 1) {
                // notify user that there are no
                // events for this calendar
                mProgress.setVisibility(View.INVISIBLE);
                calendar.setVisibility(View.VISIBLE);
            } else {
                Collection<Date> dates = new ArrayList<>();

                for (int i = 0; i < output.size(); i++) {
                    if (CollectionHelper.validDateFormat(output.get(i))) {
                        Date date = new Date(output.get(i).getStart().getDateTime().getValue());
                        dates.add(date);
                    }
                }
                calendar.highlightDates(dates);
                eventsReturnedCollection.setEvents(CollectionHelper.convertListToCollection(output));

                mProgress.setVisibility(View.INVISIBLE);
                calendar.setVisibility(View.VISIBLE);
            }
        }
    }

}