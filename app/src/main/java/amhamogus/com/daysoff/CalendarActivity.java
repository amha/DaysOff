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
package amhamogus.com.daysoff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An instance of {@link AppCompatActivity} that presents a view
 * of a users' calendar.
 */
public class CalendarActivity extends AppCompatActivity
        implements CalendarFragment.OnCalendarSelectionListener {

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_CALENDAR_NAME = "calendarName";
    private static final String PREF_CALENDAR_ID = "calendarId";
    private static final String ARG_CURRENT_DATE = "currentDate";
    String currentAccountName;
    String calendarName;
    String calendarId;
    SharedPreferences settings;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Using butterknife for data binding
        ButterKnife.bind(this);

        settings = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        currentAccountName = settings.getString(PREF_ACCOUNT_NAME, null);
        calendarName = settings.getString(PREF_CALENDAR_NAME, null);
        calendarId = settings.getString(PREF_CALENDAR_ID, null);

        toolbar.setTitle(calendarName);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
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
