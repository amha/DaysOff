package amhamogus.com.daysoff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

import amhamogus.com.daysoff.fragments.CalendarFragment;
import amhamogus.com.daysoff.fragments.CalendarSharedWithFragment;
import amhamogus.com.daysoff.model.EventCollection;

public class CalendarActivity extends AppCompatActivity
        implements CalendarSharedWithFragment.OnFragmentInteractionListener,
        CalendarFragment.OnCalendarSelectionListener {

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_CALENDAR_NAME = "calendarName";
    private static final String PREF_CALENDAR_ID = "calendarId";
    private static final String ARG_CURRENT_DATE = "currentDate";
    private static final String ARG_EVENT_LIST = "eventList";
    String currentAccountName;
    String calendarName;
    String calendarId;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        settings = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);

        currentAccountName = settings.getString(PREF_ACCOUNT_NAME, null);
        calendarName = settings.getString(PREF_CALENDAR_NAME, null);
        calendarId = settings.getString(PREF_CALENDAR_ID, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(calendarName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void onCalendarSelected(Date date, EventCollection eventCollection) {

        Bundle bundle = new Bundle();
        bundle.putLong(ARG_CURRENT_DATE, date.getTime());

        Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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

        SectionsPagerAdapter(FragmentManager fm) {
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
