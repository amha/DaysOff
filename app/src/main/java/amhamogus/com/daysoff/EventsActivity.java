package amhamogus.com.daysoff;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import amhamogus.com.daysoff.fragments.AddEventFragment;
import amhamogus.com.daysoff.fragments.EventDetailFragment;
import amhamogus.com.daysoff.model.EventCollection;

public class EventsActivity extends AppCompatActivity implements EventDetailFragment.OnFragmentInteractionListener {

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String ARG_EVENT_LIST = "eventList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String accountName = intent.getExtras().getString(PREF_ACCOUNT_NAME);

        EventCollection eventCollection = intent.getExtras().getParcelable(ARG_EVENT_LIST);
        EventDetailFragment fragment = EventDetailFragment.newInstance(accountName, eventCollection);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.event_frame, fragment).commit();
    }

    public void addEvent(View v) {

        Intent intent = new Intent(getApplicationContext(), AddEventActivity.class);
        startActivity(intent);
    }

    public void onFragmentInteraction(Uri uri) {
    }

}