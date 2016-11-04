package amhamogus.com.daysoff.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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


public class AddEventFragment extends Fragment implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener {

    EditText summary;
    TextView timeLabel;
    TextView endTimeLabel;
    TextView startTimeLabel;
    CheckBox food;
    CheckBox movie;
    CheckBox outdoors;
    Event mEvent;
    String[] checkedActivities;
    int hour;
    int minute;
    int amOrPm;

    int futureHour;
    int futureMinute;

    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_CALENDAR_ID = "calendarId";

    private static final String ARG_CURRENT_DATE = "currentDate";
    private static final String PREF_ACCOUNT_NAME = "accountName";

    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    String currentAccountName;
    String calendarId;
    Date currentDate;

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
        checkedActivities = new String[3];
        setHasOptionsMenu(true);
        mEvent = new Event();

        SharedPreferences preferences = getActivity()
                .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        currentAccountName = preferences.getString(PREF_ACCOUNT_NAME, null);
        calendarId = preferences.getString(PREF_CALENDAR_ID, null);

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

        summary = (EditText) rootView.findViewById(R.id.add_event_summary);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Get current time
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        amOrPm = c.get(Calendar.AM_PM);

        // Default Noon or Night
        String noonOrNight;
        if (amOrPm == 0) {
            noonOrNight = "AM";
        } else {
            noonOrNight = "PM";
        }

        startTimeLabel = (TextView) rootView.findViewById(R.id.start_time_btn);
        endTimeLabel = (TextView) rootView.findViewById(R.id.end_time_btn);

        if (minute < 29) {
            minute = 30;
            futureMinute = 00;

            if (hour == 12) {
                hour = 1;
                futureHour = hour;
            } else {

                futureHour = 1 + hour;
            }
        } else {
            minute = 00;
            futureMinute = 30;

            if (hour == 12) {
                hour = 1;
                futureHour = hour;
            } else {
                hour = hour + 1;
                futureHour = hour;
            }
        }

        startTimeLabel.setText("" + hour + ":"
                + String.format("%02d", minute) + " " + noonOrNight);
        endTimeLabel.setText("" + futureHour + ":"
                + String.format("%02d", futureMinute) + " " + noonOrNight);

        startTimeLabel.setOnClickListener(this);
        endTimeLabel.setOnClickListener(this);

        food = (CheckBox) rootView.findViewById(R.id.event_checkbox_food);
        food.setOnClickListener(this);

        movie = (CheckBox) rootView.findViewById(R.id.event_checkbox_movie);
        movie.setOnClickListener(this);

        outdoors = (CheckBox) rootView.findViewById(R.id.event_checkbox_outdoors);
        outdoors.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // When user hits save in the toolbar validate input, and
        // build event object and send insert event request to google.
        if (id == R.id.action_save_event) {
            if (validate()) {

                // Convert time into RFC3339 format
                DateTime start = DateFormater.getDateTime(currentDate, 0);
                EventDateTime startDateTime = new EventDateTime()
                        .setDateTime(start)
                        .setTimeZone(Calendar.getInstance().getTimeZone().getID());

                DateTime end = DateFormater.getDateTime(currentDate, 1);
                EventDateTime endDateTime = new EventDateTime()
                        .setDateTime(end)
                        .setTimeZone(Calendar.getInstance().getTimeZone().getID());

                // Populate event object with user input
                mEvent.setSummary(summary.getText().toString());
                mEvent.setStart(startDateTime).setEnd(endDateTime);

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
                    checkedActivities[0] = "Grub";
                } else {
                    checkedActivities[0] = "";
                }
                break;
            case R.id.event_checkbox_movie:
                if (((CheckBox) view).isChecked()) {
                    checkedActivities[1] = "Flick";
                } else {
                    checkedActivities[1] = "";
                }
                break;
            case R.id.event_checkbox_outdoors:
                if (((CheckBox) view).isChecked()) {
                    checkedActivities[2] = "Walking around";
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

        ViewGroup timePickerGroup = (ViewGroup) timePicker.getChildAt(0);
        //String amOrPm = ((Button)timePickerGroup.getChildAt(2)).getText().toString();

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR, hour);

        hour = pickerHour;
        minute = pickerMinute;

        if (hour == 0) {
            displayHour = "12";
        } else {
            displayHour = hour + "";
        }
        if (minute < 10) {
            displayMinute = "0" + minute;
        } else {
            displayMinute = minute + "";
        }

        String amOrPm = ((calendar.get(Calendar.AM_PM)) == Calendar.AM) ? "am" : "pm";
        timeLabel.setText(displayHour + ":" + displayMinute + " " + amOrPm);
    }

    private boolean validate() {
        if (summary.getText().length() < 1) {
            summary.setError(getResources().getString(R.string.event_form_error_summary));
        }

        return true;
    }

    private class AddEventTask extends AsyncTask<Event, Void, String> {

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
        protected String doInBackground(Event... events) {
            try {
                insertEventData(events[0]);
            } catch (Exception e) {
                cancel(true);
            }
            return null;
        }

        private void insertEventData(Event event) throws IOException {
            // Send insert command to backend
            Event responseEvent = mService.events().insert(calendarId, event).execute();
        }

        @Override
        protected void onPostExecute(String output) {
            Intent intent = new Intent(getActivity().getApplicationContext(), EventsActivity.class);
            intent.putExtra(ARG_CURRENT_DATE, currentDate.getTime());
            startActivity(intent);
            Toast.makeText(getContext(), "Added Event!", Toast.LENGTH_SHORT).show();
        }
    }
}