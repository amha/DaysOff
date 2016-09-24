package amhamogus.com.daysoff.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import amhamogus.com.daysoff.R;

public class EventsActivity extends AppCompatActivity implements EventDetailFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String accountName = getIntent().getExtras().getString("NAME");

        EventDetailFragment fragment = EventDetailFragment.newInstance(accountName);
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.event_frame, fragment).commit();
    }


    public void onFragmentInteraction(Uri uri) {

    }

}
