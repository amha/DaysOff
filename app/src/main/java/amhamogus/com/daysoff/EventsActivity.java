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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import amhamogus.com.daysoff.fragments.EventDetailFragment;
import amhamogus.com.daysoff.model.DaysOffEvent;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An instance of {@link AppCompatActivity} that displays a list of
 * {@link DaysOffEvent} items.
 */
public class EventsActivity extends AppCompatActivity
        implements EventDetailFragment.OnEventSelected {

    private static final String ARG_CURRENT_DATE = "currentDate";
    long selectedDate;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Using butterknife for data binding
        ButterKnife.bind(this);

        Intent intent = getIntent();
        selectedDate = intent.getExtras().getLong(ARG_CURRENT_DATE);
        setSupportActionBar(toolbar);

        // Set user selected date as the Toolbar title to provide
        // additional context for the user.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(new Date(selectedDate).toString().substring(0, 10));
        }

        EventDetailFragment fragment;
        fragment = EventDetailFragment.newInstance(selectedDate);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.event_frame, fragment).commit();
    }

    /**
     * Click handler that launches {@link AddEventActivity}
     * when the user taps the FAB.
     *
     * @param v The view that was selected.
     */
    public void addEvent(View v) {
        Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
        intent.putExtra(ARG_CURRENT_DATE, selectedDate);
        startActivity(intent);
    }

    /**
     * Launches  {@link SingleEventActivity} when the user selects a
     * row item, that represents a single event.
     *
     * @param message TODO
     * @param event   An instance of {@link DaysOffEvent}.
     */
    public void onFragmentInteraction(String message, DaysOffEvent event) {

        Bundle args = new Bundle();
        args.putString("DESC", event.getDesc());
        args.putString("TIME", event.getTimeRange());
        args.putString("LOCATION", event.getLocation());

        Intent intent = new Intent(getApplicationContext(), SingleEventActivity.class);
        intent.putExtras(args);
        startActivity(intent);
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
}
