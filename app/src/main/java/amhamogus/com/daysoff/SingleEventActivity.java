package amhamogus.com.daysoff;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import amhamogus.com.daysoff.fragments.SingleEventFragment;

public class SingleEventActivity extends AppCompatActivity {

    String desc;
    String startTime;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        Bundle args = getIntent().getExtras();
        if(args != null) {

            desc = args.getString("DESC");
            startTime = args.getString("TIME");
            location = args.getString("LOCATION");


            SingleEventFragment fragment = SingleEventFragment.newInstance(desc, startTime,location);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.single_event_wrapper, fragment).commit();
        }
    }
}
