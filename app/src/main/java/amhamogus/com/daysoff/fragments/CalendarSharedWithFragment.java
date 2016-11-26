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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.adapters.ContactsAdapter;

public class CalendarSharedWithFragment extends Fragment {

    private static final String TAG = "SHARED WITH FRAGMENT";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static final String ARG_ACCNT_NAME = "param1";
    GoogleAccountCredential mCredential;
    ListView contactList;
    private String accountName;

    public CalendarSharedWithFragment() {
    }

    public static CalendarSharedWithFragment newInstance(String accountName) {
        CalendarSharedWithFragment fragment = new CalendarSharedWithFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCNT_NAME, accountName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            accountName = getArguments().getString(ARG_ACCNT_NAME);
        }
        mCredential = GoogleAccountCredential.usingOAuth2(getActivity()
                .getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(accountName)
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calendar_shared, container, false);
        contactList = (ListView) rootView.findViewById(R.id.contact_list);

        new GetSharedContactsTask(mCredential).execute();
        return rootView;
    }

    private class GetSharedContactsTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private com.google.api.services.calendar.Calendar mACLService = null;

        GetSharedContactsTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mACLService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off")
                    .build();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> sharedWithDetails = new ArrayList<>();
            try {
                sharedWithDetails = getSharedWith();
            } catch (IOException io) {
                Log.d(TAG, io.toString());
            }
            return sharedWithDetails;
        }

        private ArrayList<String> getSharedWith() throws IOException {
            Acl acl = mACLService.acl().list("primary").execute();
            List<AclRule> rules = acl.getItems();
            ArrayList<String> emailList = new ArrayList<>();
            for (AclRule rule : rules) {
                emailList.add(rule.getScope().getValue());
            }
            return emailList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> output) {
            ContactsAdapter adapter = new ContactsAdapter(getContext(), output);
            contactList.setAdapter(adapter);
        }
    }
}
