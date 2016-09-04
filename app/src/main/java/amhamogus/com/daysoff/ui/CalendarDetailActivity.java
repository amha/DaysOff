package amhamogus.com.daysoff.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
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
import com.google.api.services.calendar.model.Events;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import amhamogus.com.daysoff.R;

public class CalendarDetailActivity extends AppCompatActivity {

    final String TAG = "CANENDAR_ACTIVITY_TAG";
    static CalendarPickerView calendar;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * The key for the list parameter
     */
    String ARG_CALENDAR_ID = "id";


    GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Initialize credentials and service object.
        Bundle extras = getIntent().getExtras();
        String accountName = extras.getString(PREF_ACCOUNT_NAME);

        mCredential = GoogleAccountCredential
                .usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(accountName)
                .setBackOff(new ExponentialBackOff());

        new RequestEventsTask(mCredential).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_calendar_detail, container, false);

            Calendar nextYear = Calendar.getInstance();
            nextYear.add(Calendar.YEAR, 1);
            Date today = new Date();

            calendar = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
            calendar.init(today, nextYear.getTime())
                    .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                    .withSelectedDate(today);

            calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                @Override
                public void onDateSelected(Date date) {
                    //Toast.makeText(getContext(),"date: " + date.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), EventDetailActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onDateUnselected(Date date) {

                }
            });

            //Hook into calendar widget
            List<CalendarCellDecorator> decoratorList = new ArrayList<>();
            decoratorList.add(new DayDecorator());
            calendar.setDecorators(decoratorList);

            return rootView;
        }
    }

    public static class ContactsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ContactsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ContactsFragment newInstance(int sectionNumber) {
            ContactsFragment fragment = new ContactsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_calendar_detail_contacts, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return ContactsFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Calendar";
                case 1:
                    return "Contacts";
            }
            return null;
        }
    }

    /**
     * Request a list of events for a given calendar.
     */
    private class RequestEventsTask extends AsyncTask<Void, Void, List<Event>> {

        private com.google.api.services.calendar.Calendar mService = null;

        public RequestEventsTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off")
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

            // Iterate over the events in the specified calendar
            do {
                events = mService.events().list("primary")
                        .setPageToken(pageToken)
                        .setTimeMin(now)
                        .setMaxResults(20)
                        .execute();
                items = events.getItems();
                pageToken = events.getNextPageToken();
            } while (pageToken != null);
            return items;
        }

        @Override
        protected void onPreExecute() {
            // Do nothing
        }

        @Override
        protected void onPostExecute(List<Event> output) {
            if (output == null || output.size() == 0) {
                // Show toast when the server doesn't return anything
                // mOutputText.makeText(getApplicationContext(), "No results returned.", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Empty", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(getApplicationContext(), output.size() + "", Toast.LENGTH_SHORT).show();

                Collection<Date> dates = new ArrayList<Date>();

                for (int i = 0; i < output.size(); i++) {

                    Log.d(TAG, "collection value: " + i +" : " +output.get(i).getStart().getDateTime().getValue());
                    //    String temp = output.get(i).getStart().getDate().toString();

                    //              Date date = new SimpleDateFormat("yyyy-MM-dd").parse(temp);
                    Date date = new Date(output.get(i).getStart().getDateTime().getValue());
                    dates.add(date);
                }

                calendar.highlightDates(dates);
//                returnedCalendarList = output;
//                if (returnedCalendarList != null) {
//                    mList = CalendarItemFragment.newInstance(1, returnedCalendarList);
//                }
//
//                // Add fragment to main activity when we're retrieved
//                // data from the the server
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.list_wrapper, mList).commit();
            }
        }

    }
}
