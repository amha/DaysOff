package amhamogus.com.daysoff.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

import amhamogus.com.daysoff.R;


public class AddEventFragment extends Fragment {

    public AddEventFragment() {
    }

    public static AddEventFragment newInstance() {
        AddEventFragment fragment = new AddEventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TextView startTimeLabel = (TextView)rootView.findViewById(R.id.start_time_btn);
        startTimeLabel.setText("" + hour + ":" + minute);

        TextView endTimeLabel = (TextView)rootView.findViewById(R.id.end_time_btn);
        endTimeLabel.setText("" + hour + ":" + minute);

        return rootView;
    }
}
