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
package amhamogus.com.daysoff.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import amhamogus.com.daysoff.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleEventFragment extends Fragment {

    String desc;
    String startTime;
    String location;
    @BindView(R.id.single_event_desc_field)
    TextView mDesc;
    @BindView(R.id.single_event_time_field)
    TextView mStartTime;
    @BindView(R.id.single_event_location_field)
    TextView mLocation;

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

        if (getArguments() != null) {
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
        if (mDesc != null && startTime != null && location != null) {
            mDesc.setText(desc);
            mStartTime.setText(startTime);
            mLocation.setText(location);
        }
        return root;
    }

}
