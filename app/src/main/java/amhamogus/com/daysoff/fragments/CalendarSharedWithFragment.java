package amhamogus.com.daysoff.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarSharedWithFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarSharedWithFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarSharedWithFragment extends Fragment {

    String TAG = "SHARED_WITH_FRAGMENT";

    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private OnFragmentInteractionListener mListener;
    ListView contactList;

    public CalendarSharedWithFragment() {
    }

    public static CalendarSharedWithFragment newInstance(String accountName) {
        CalendarSharedWithFragment fragment = new CalendarSharedWithFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, accountName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            Log.d(TAG, "PARAM1 = " + mParam1);
        }

        mCredential = GoogleAccountCredential
                .usingOAuth2(getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(mParam1)
                .setBackOff(new ExponentialBackOff());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calendar_shared, container, false);
        contactList = (ListView)rootView.findViewById(R.id.contact_list);

        new GetSharedContactsTask(mCredential).execute();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onContactSelected(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onContactSelected(Uri uri);
    }

    private class GetSharedContactsTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private com.google.api.services.calendar.Calendar mACLService = null;

        public GetSharedContactsTask(GoogleAccountCredential credential) {

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
                Log.d(TAG, "error = " + io.toString());
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
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(ArrayList<String> output) {
            List<String> list = output;
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getActivity().getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    list);
            contactList.setAdapter(arrayAdapter);

        }
    }
}
