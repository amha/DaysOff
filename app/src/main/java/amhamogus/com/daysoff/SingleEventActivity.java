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
package amhamogus.com.daysoff;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import amhamogus.com.daysoff.fragments.SingleEventFragment;

/**
 * An intance of {@link AppCompatActivity} that represents a
 * detailed view of a single {@link amhamogus.com.daysoff.model.DaysOffEvent}.
 */
public class SingleEventActivity extends AppCompatActivity {

    String desc;
    String startTime;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);

        Bundle args = getIntent().getExtras();
        if (args != null) {

            desc = args.getString("DESC");
            startTime = args.getString("TIME");
            location = args.getString("LOCATION");

            SingleEventFragment fragment =
                    SingleEventFragment.newInstance(desc, startTime, location);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.single_event_wrapper, fragment).commit();
        }
    }
}
