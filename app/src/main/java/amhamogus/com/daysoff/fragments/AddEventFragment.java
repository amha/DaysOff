package amhamogus.com.daysoff.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import amhamogus.com.daysoff.R;


public class AddEventFragment extends Fragment implements View.OnClickListener,
        TimePickerDialog.OnTimeSetListener {

    TextView timeLabel;

    private final String REQUEST_TIME = "requestTime";

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

        TextView startTimeLabel = (TextView) rootView.findViewById(R.id.start_time_btn);
        startTimeLabel.setText("" + hour + ":" + minute);
        startTimeLabel.setOnClickListener(this);

        TextView endTimeLabel = (TextView) rootView.findViewById(R.id.end_time_btn);
        endTimeLabel.setText("" + hour + ":" + minute);
        endTimeLabel.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(this, 0);
        timeLabel = (TextView)view;
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        timeLabel.setText(i + ":" + i1);
    }
}