package amhamogus.com.daysoff.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import amhamogus.com.daysoff.R;


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

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String ARG_LOCAL_OR_REMOTE = "remote";
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    private static String currentAccountName;

    private final String REQUEST_TIME = "requestTime";

    public AddEventFragment() {
    }

    public static AddEventFragment newInstance() {
        return new AddEventFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkedActivities = new String[3];
        setHasOptionsMenu(true);
        mEvent = new Event();

        SharedPreferences preferences = getActivity()
                .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        currentAccountName = preferences.getString(PREF_ACCOUNT_NAME, null);

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

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        startTimeLabel = (TextView) rootView.findViewById(R.id.start_time_btn);
        startTimeLabel.setText("" + hour + ":" + minute);
        startTimeLabel.setOnClickListener(this);

        endTimeLabel = (TextView) rootView.findViewById(R.id.end_time_btn);
        endTimeLabel.setText("" + hour + ":" + minute);
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
                DateTime start = new DateTime("2016-10-11T01:00:11");
                EventDateTime startDateTime = new EventDateTime()
                        .setDateTime(start)
                        .setTimeZone(Calendar.getInstance().getTimeZone().getID());

                DateTime end = new DateTime("2016-10-11T01:30:55");
                EventDateTime endDateTime = new EventDateTime()
                        .setDateTime(end)
                        .setTimeZone(Calendar.getInstance().getTimeZone().getID());

                // Populate event object with user input
                mEvent.setSummary(summary.getText().toString());
                mEvent.setStart(startDateTime).setEnd(endDateTime);

                new AddEventTask(mCredential).execute(mEvent);

            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
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
            case R.id.end_time_btn:
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.setTargetFragment(this, 0);
                timeLabel = (TextView) view;
                newFragment.show(getFragmentManager(), "timePicker");
                break;
        }
        Log.d("AMHA", "output: "
                + checkedActivities[0] + " : "
                + checkedActivities[1] + ": "
                + checkedActivities[2] + " : "
                + startTimeLabel.getText().toString() + " : "
                + endTimeLabel.getText().toString());
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        String displayHour;
        String displayMinute;
        String amOrPm;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.HOUR, hour);

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

        amOrPm = ((calendar.get(Calendar.AM_PM)) == Calendar.AM) ? "am" : "pm";
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
            // Send insert command
            Event responseEvent = mService.events().insert("primary", event).execute();
            if (responseEvent != null) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Successfully added new event",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "Error adding event. =(",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(String output) {

        }
    }
}