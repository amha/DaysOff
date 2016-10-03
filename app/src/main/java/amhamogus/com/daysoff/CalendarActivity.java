package amhamogus.com.daysoff;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import amhamogus.com.daysoff.fragments.CalendarFragment;
import amhamogus.com.daysoff.fragments.CalendarSharedWithFragment;
import amhamogus.com.daysoff.model.DaysOffEvent;
import amhamogus.com.daysoff.model.EventCollection;

public class CalendarActivity extends AppCompatActivity
        implements CalendarSharedWithFragment.OnFragmentInteractionListener, CalendarFragment.OnCalendarSelectionListener {

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

    private static final String ARG_CALENDAR_ID = "id";
    private static final String ARG_ACCOUNT_NAME = "accountName";
    private static final String ARG_CALENDAR_NAME = "calendarName";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String ARG_EVENT_LIST = "eventList";

    private static String currentAccountName;
    private static String calendarId;
    private static String calendarName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Bundle extras = getIntent().getExtras();
        currentAccountName = extras.getString(ARG_ACCOUNT_NAME);
        calendarId = extras.getString(ARG_CALENDAR_ID);
        calendarName = extras.getString(ARG_CALENDAR_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(calendarName);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    public void onCalendarSelected(Date date, EventCollection eventCollection) {

        Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
        Bundle bundle = new Bundle();

        String input = eventCollection.getEvents().get(0).getStartTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date eventDate;

        List<DaysOffEvent> eventsOnASelectedDate = new ArrayList<DaysOffEvent>();

        // Find events from the events collection that
        // match to user selected calendar date
        for (int i = 0; i < eventCollection.getEvents().size(); i++) {
            try {
                eventDate = format.parse(eventCollection.getEvents().get(i).getStartTime());
                if (date.compareTo(eventDate) == 0) {
                    eventsOnASelectedDate.add(eventCollection.getEvents().get(i));
                }
            } catch (java.text.ParseException e) {
                Log.d("AMHA", "Error parsing date: " + e);
            }
        }

        EventCollection collectionOnASingleDay = new EventCollection(eventsOnASelectedDate);
        bundle.putString(PREF_ACCOUNT_NAME, currentAccountName);
        bundle.putParcelable(ARG_EVENT_LIST, collectionOnASingleDay);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onContactSelected(Uri uri) {
        // Do nothing, this may not be needed
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
            // Return a CalendarFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return CalendarFragment.newInstance(position + 1);
                case 1:
                    return CalendarSharedWithFragment.newInstance(currentAccountName);
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
}
