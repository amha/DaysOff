package amhamogus.com.daysoff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import amhamogus.com.daysoff.ui.CalendarDetailActivity;
import amhamogus.com.daysoff.ui.CalendarItemFragment;
import amhamogus.com.daysoff.ui.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements CalendarItemFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        //TODO: Pass Calendar ID to CalendarDetailActivity
//        if(item != null){
//            Toast.makeText(MainActivity.this,  "Item number:" +item.id,
//                    Toast.LENGTH_SHORT).show();
//        }

        Intent intent = new Intent(getApplicationContext(), CalendarDetailActivity.class);
        startActivity(intent);
    }
}
