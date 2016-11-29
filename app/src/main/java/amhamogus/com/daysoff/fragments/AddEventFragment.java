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

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import amhamogus.com.daysoff.EventsActivity;
import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.utils.DateFormater;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An instance of {@link Fragment} that presents a form to the
 * user.
 */
public class AddEventFragment extends Fragment implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_CALENDAR_ID = "calendarId";
    private static final String ARG_CURRENT_DATE = "currentDate";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_CALENDAR_NAME = "calendarName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    final String TAG = "ADD_FRAGMENT_LOG";
    TextView timeLabel;
    Event mEvent;
    String[] checkedActivities;
    int hour;
    int minute;
    int amOrPm;
    DateTime start;
    DateTime end;
    String noonOrNight;
    int futureHour;
    int futureMinute;
    String currentAccountName;
    String calendarId;
    Date currentDate;
    @BindView(R.id.add_event_summary)
    EditText summary;
    @BindView(R.id.add_event_date)
    TextView addEventHeader;
    @BindView(R.id.start_time_btn)
    TextView startTimeLabel;
    @BindView(R.id.end_time_btn)
    TextView endTimeLabel;
    @BindView(R.id.event_checkbox_food)
    CheckBox food;
    @BindView(R.id.event_checkbox_movie)
    CheckBox movie;
    @BindView(R.id.event_checkbox_outdoors)
    CheckBox outdoors;
    @BindView(R.id.add_event_location)
    EditText location;
    GoogleAccountCredential mCredential;


    public AddEventFragment() {
    }

    public static AddEventFragment newInstance(Date date) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CURRENT_DATE, date.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            currentDate = new Date();
            currentDate.setTime(getArguments().getLong(ARG_CURRENT_DATE));
        }

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        checkedActivities = new String[3];
        mEvent = new Event();

        SharedPreferences preferences = getActivity()
                .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        currentAccountName = preferences.getString(PREF_ACCOUNT_NAME, null);
        calendarId = "Calendar Name: " + preferences.getString(PREF_CALENDAR_NAME, null);

        mCredential = GoogleAccountCredential
                .usingOAuth2(getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(currentAccountName)
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);
        ButterKnife.bind(this, rootView);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        addEventHeader.setText(calendarId);

        // Set default date and time values
        // based on the current time
        final Calendar currentDate = Calendar.getInstance();
        hour = currentDate.get(Calendar.HOUR);
        minute = currentDate.get(Calendar.MINUTE);
        amOrPm = currentDate.get(Calendar.AM_PM);

        if (amOrPm == Calendar.AM) {
            noonOrNight = "AM";
        } else {
            noonOrNight = "PM";
        }

        // By default, events are set to 30 minutes. Based on the
        // current minute value round to the next 30 minute set
        if (minute <= 29) {
            // Minute value is between 0 and 29.
            // Set the start time to half past.
            minute = 30;
            // Set end time to the the next hour.
            futureMinute = 0;
            // Set future hour to the next hour.
            if (hour > 12) {
                // The app tries to mange hours in 12 hour format
                // So hour values should not exceed 12. If hour does
                // exceed 12, we convert to 12 hour format.
                hour = hour - 12;
                futureHour = hour + 1;
            } else if (hour == 0) {
                // In 12 hour format. Convert 0 to 12.
                // Noon or midnight is set to '0' which is not
                // helpful for users, thus we change 0 to 12
                hour = 12;
                futureHour = 1;
            } else {
                //
                futureHour = hour + 1;
            }
        } else {
            // Current minute is between 30 and 60.
            // Round to the nearest hour.
            minute = 0;
            futureMinute = 30;

            if (hour > 12) {
                // The app tries to mange hours in 12 hour format
                // So hour values should not exceed 12. If hour does
                // exceed 12, we convert to 12 hour format.
                hour = hour - 12;
                futureHour = hour;
            } else if (hour == 0 || hour == 12) {
                // In 12 hour format. Convert 0 to 12.
                // Noon or midnight is set to '0' which is not
                // helpful for users, thus we change 0 to 12
                hour = 1;
                futureHour = 1;
            } else {
                hour = hour + 1;
                futureHour = hour;
            }
        }

        // Update form with default start and end time
        // for a new event.
        startTimeLabel.setText("" + hour + ":"
                + String.format("%02d", minute) + " " + noonOrNight);
        startTimeLabel.setOnClickListener(this);
        endTimeLabel.setText("" + futureHour + ":"
                + String.format("%02d", futureMinute) + " " + noonOrNight);
        endTimeLabel.setOnClickListener(this);

        // Add click listener to checkboxes
        food.setOnClickListener(this);
        movie.setOnClickListener(this);
        outdoors.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // User has tapped the home icon
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.action_save_event:
                // User is attempting to add a new event. Upon validating
                // the form, send an insert event to server.
                if (validate()) {
                    // Convert time into RFC3339 format
                    start = DateFormater.getDateTime(
                            startTimeLabel.getText().toString(),
                            currentDate,
                            noonOrNight);

                    EventDateTime startDateTime = new EventDateTime()
                            .setDateTime(start)
                            .setTimeZone(Calendar.getInstance().getTimeZone().getID());

                    end = DateFormater.getDateTime(
                            endTimeLabel.getText().toString(),
                            currentDate,
                            noonOrNight);

                    EventDateTime endDateTime = new EventDateTime()
                            .setDateTime(end)
                            .setTimeZone(Calendar.getInstance().getTimeZone().getID());

                    // Populate event object with user input
                    mEvent.setSummary(summary.getText().toString());
                    mEvent.setStart(startDateTime).setEnd(endDateTime);
                    mEvent.setLocation(location.getText().toString());

                    String decription = "";
                    if (movie.isChecked()) {
                        decription = movie.getText().toString();
                    }
                    if (food.isChecked()) {
                        decription = decription + food.getText().toString();
                    }
                    if (outdoors.isChecked()) {
                        decription = decription + outdoors.getText().toString();
                    }
                    mEvent.setDescription(decription);
                    new AddEventTask(mCredential).execute(mEvent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Bundle args;
        DialogFragment timePicker;

        switch (view.getId()) {
            case R.id.event_checkbox_food:
                if (((CheckBox) view).isChecked()) {
                    checkedActivities[0] = "Eat a meal";
                } else {
                    checkedActivities[0] = "";
                }
                break;
            case R.id.event_checkbox_movie:
                if (((CheckBox) view).isChecked()) {
                    checkedActivities[1] = "Watch a movie";
                } else {
                    checkedActivities[1] = "";
                }
                break;
            case R.id.event_checkbox_outdoors:
                if (((CheckBox) view).isChecked()) {
                    checkedActivities[2] = "Go for a walk";
                } else {
                    checkedActivities[2] = "";
                }
                break;
            case R.id.start_time_btn:
                args = new Bundle();
                args.putInt("HOUR", hour);
                args.putInt("MINUTE", minute);

                timeLabel = (TextView) view;
                timePicker = new TimePickerFragment();
                timePicker.setArguments(args);
                timePicker.setTargetFragment(this, 0);
                timePicker.show(getFragmentManager(), "timePicker");
                break;
            case R.id.end_time_btn:
                args = new Bundle();
                args.putInt("HOUR", futureHour);
                args.putInt("MINUTE", futureMinute);

                timeLabel = (TextView) view;
                timePicker = new TimePickerFragment();

                timePicker.setArguments(args);
                timePicker.setTargetFragment(this, 0);
                timePicker.show(getFragmentManager(), "timePicker");
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int pickerHour, int pickerMinute) {

        String displayHour;
        String displayMinute;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, pickerMinute);
        calendar.set(Calendar.HOUR_OF_DAY, pickerHour);

        hour = pickerHour;
        minute = pickerMinute;

        if (hour == 0) {
            displayHour = "12";
            noonOrNight = "AM";
        } else if (hour > 12) {
            displayHour = (hour - 12) + "";
            noonOrNight = "PM";
        } else {
            noonOrNight = "AM";
            displayHour = hour + "";
        }

        if (minute < 10) {
            displayMinute = "0" + minute;
        } else {
            displayMinute = minute + "";
        }
        // Add AM or PM to the user selected time range
        String amOrPm = ((calendar.get(Calendar.AM_PM)) == Calendar.AM) ? "am" : "pm";
        timeLabel.setText(displayHour + ":" + displayMinute + " " + amOrPm);
    }

    // Performs form validation before adding event to calendar
    private boolean validate() {
        // Check event title exists
        if (summary.getText().length() < 1) {
            summary.setError(getResources().getString(R.string.event_form_error_summary));
            return false;
        }

        // Check start time proceeds end time
        if (start == null) {
            start = DateFormater.getDateTime(startTimeLabel.getText().toString(),
                    currentDate, noonOrNight);
        }
        if (end == null) {
            end = DateFormater.getDateTime(endTimeLabel.getText().toString(),
                    currentDate, noonOrNight);
        }
        Date startDate = new Date(start.getValue());
        Date endDate = new Date(end.getValue());

        if (startDate.after(endDate)) {
            AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle("Event time error")
                    .setMessage("Start times must proceed end times. Pick new time.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = mAlertBuilder.create();
            alertDialog.show();
            return false;
        }

        if ((food.isChecked() || movie.isChecked() || outdoors.isChecked()) == false) {
            AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(getActivity())
                    .setTitle("Select an activity")
                    .setMessage("You must select at least 1 activity.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = mAlertBuilder.create();
            alertDialog.show();
            return false;
        }
        return true;
    }

    // Inserts a new event object into the selected calendar
    private class AddEventTask extends AsyncTask<Event, Void, Event> {

        private com.google.api.services.calendar.Calendar mService = null;

        AddEventTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off - Debug")
                    .build();
        }

        @Override
        protected Event doInBackground(Event... events) {
            Event mEvent = null;
            try {
                mEvent = insertEventData(events[0]);
            } catch (Exception e) {
                cancel(true);
                Log.d(TAG, "error" + e.toString());
                // build dialog to inform user their event wasn't// successfully added
            }
            return mEvent;
        }

        private Event insertEventData(Event event) throws IOException {
            // Send insert command to backend
            return mService.events().insert(calendarId, event).execute();
        }

        @Override
        protected void onPostExecute(Event output) {
            if (output == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.add_event_network_error_title)
                        .setMessage(R.string.add_event_network_error_message)
                        .setPositiveButton(R.string.calendar_positive_button,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                builder.show();
            } else {
                Intent intent = new Intent(getContext(), EventsActivity.class);
                intent.putExtra(ARG_CURRENT_DATE, currentDate.getTime());
                startActivity(intent);
                Toast.makeText(getContext(), "Added Event!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}