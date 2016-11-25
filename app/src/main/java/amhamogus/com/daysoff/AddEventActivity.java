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

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import java.util.Date;

import amhamogus.com.daysoff.fragments.AddEventFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An instance of {@link AppCompatActivity} that presents a form
 * that's used to create a new instance of {@link amhamogus.com.daysoff.model.DaysOffEvent}
 */
public class AddEventActivity extends AppCompatActivity {

    private static final String ARG_CURRENT_DATE = "currentDate";
    AddEventFragment addEventFragment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Using butterknife for data binding
        ButterKnife.bind(this);

        // Set activity title to date, to provide users additional cotext
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
