package amhamogus.com.daysoff.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import amhamogus.com.daysoff.R;
import butterknife.ButterKnife;

public class SingleEventFragment extends Fragment {

    String desc;
    String startTime;
    String location;

    public SingleEventFragment() {
    }

    public static SingleEventFragment newInstance(String eDesc, String eStartTime, String location) {
        Bundle args = new Bundle();
        args.putString("DESC", eDesc);
        args.putString("TIME", eStartTime);
        args.putString("LOCATION", location);

        SingleEventFragment fragment = new SingleEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            desc = getArguments().getString("DESC");
            startTime = getArguments().getString("TIME");
            location = getArguments().getString("LOCATION");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_single_event, container, false);
        ButterKnife.bind(root);

        TextView mDesc = (TextView)root.findViewById(R.id.single_event_desc_field);
        mDesc.setText(desc);

        TextView mStartTime = (TextView)root.findViewById(R.id.single_event_time_field);
        mStartTime.setText(startTime);

        TextView mLocation = (TextView) root.findViewById(R.id.single_event_location_field);
        mLocation.setText(location);

        return root;
    }

}
