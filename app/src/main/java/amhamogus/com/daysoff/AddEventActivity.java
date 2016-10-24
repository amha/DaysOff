package amhamogus.com.daysoff;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.Date;

import amhamogus.com.daysoff.fragments.AddEventFragment;

public class AddEventActivity extends AppCompatActivity {

    AddEventFragment addEventFragment;
    private static final String ARG_CURRENT_DATE = "currentDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Date cDate = new Date();
        cDate.setTime(getIntent().getExtras().getLong(ARG_CURRENT_DATE));
        toolbar.setTitle(cDate.toString().substring(0,10));

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(
                getResources().getDrawable(R.drawable.ic_close_white_24dp, null));

        addEventFragment = AddEventFragment.newInstance(cDate);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.add_event_form, addEventFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return false;
    }
}
