package amhamogus.com.daysoff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Date;

import amhamogus.com.daysoff.fragments.EventDetailFragment;
import amhamogus.com.daysoff.model.DaysOffEvent;

public class EventsActivity extends AppCompatActivity
        implements EventDetailFragment.OnEventSelected {

    private static final String ARG_CURRENT_DATE = "currentDate";

    private final String TAG = "EVENTS ACTIVITY";
    long selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        selectedDate = intent.getExtras().getLong(ARG_CURRENT_DATE);

        if (getSupportActionBar() != null) {
            getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()
                    .setTitle(new Date(selectedDate).toString().substring(0, 10));
        }

        EventDetailFragment fragment;
        fragment = EventDetailFragment.newInstance(selectedDate);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.event_frame, fragment).commit();
    }

    public void addEvent(View v) {
        Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
        intent.putExtra(ARG_CURRENT_DATE, selectedDate);
        startActivity(intent);
    }

    public void onFragmentInteraction(String message, DaysOffEvent event) {

        Intent intent = new Intent(getApplicationContext(), SingleEventActivity.class);
        Bundle args = new Bundle();
        args.putString("DESC", event.getDesc());
        args.putString("TIME", event.getTimeRange());
        args.putString("LOCATION", event.getLocation());
        intent.putExtras(args);
        startActivity(intent);
    }
}
