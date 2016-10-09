package amhamogus.com.daysoff.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.adapters.CalendarItemRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MainListFragment extends Fragment {

    public Toast mOutputText;
    final String TAG = "AMHA-MAIN-FRAGMENT";

    GoogleAccountCredential mCredential;
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ArrayList<String> calendarID;
    ArrayList<String> idArray;

    private List<CalendarListEntry> returnedCalendarList;
    private ArrayList<String> mCalendarList;
    private RecyclerView recyclerView;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_FILE = "calendarSessionData";
    private static final String ARG_COLUMN_COUNT = "columnCount";
    private static final String ARG_CALENDAR_LIST = "list";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};


    public MainListFragment() {
    }

    @SuppressWarnings("unused")
    public static MainListFragment newInstance(int columnCount) {

        MainListFragment fragment = new MainListFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        args.putStringArrayList(ARG_CALENDAR_LIST, calendarListArray);
//        args.putStringArrayList("temp", idArray);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        SharedPreferences pref =
                getActivity().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String name = pref.getString(PREF_ACCOUNT_NAME, null);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(name)
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_calendar, container, false);
        recyclerView = (RecyclerView) view;

        if(savedInstanceState == null) {
            new RequestCalendarListTask(mCredential).execute();
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Check that the activity implements the interaction handler
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        void onCalendarSelectedInteraction(String item, String name);
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class RequestCalendarListTask extends AsyncTask<Void, Void, List<CalendarListEntry>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public RequestCalendarListTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off - Debug")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<CalendarListEntry> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private List<CalendarListEntry> getDataFromApi() throws IOException {
            CalendarList mList = mService.calendarList().list().execute();
            return mList.getItems();
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "calling google api");
        }

        @Override
        protected void onPostExecute(List<CalendarListEntry> output) {
            if (output == null || output.size() == 0) {
                // Show toast when the server doesn't return anything
                mOutputText.makeText(getActivity().getApplicationContext(),
                        "No results returned.", Toast.LENGTH_SHORT).show();
            } else {
                returnedCalendarList = output;
                if (returnedCalendarList != null) {

                    ArrayList<String> calendarListArray = new ArrayList<String>(output.size());
                    idArray = new ArrayList<>(output.size());

                    for (int i = 0; i < output.size(); i++) {
                        calendarListArray.add(output.get(i).getSummary());
                        idArray.add(output.get(i).getId());
                    }
                    if (mColumnCount <= 1) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(
                                getContext()));
                    } else {
                        recyclerView.setLayoutManager(new GridLayoutManager(
                                getContext(), mColumnCount));
                    }
                    recyclerView.setAdapter(new CalendarItemRecyclerViewAdapter(
                            calendarListArray, idArray, mListener));
                }
            }
        }
    }
}
