package amhamogus.com.daysoff.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.adapters.EventsRecyclerViewAdapter;
import amhamogus.com.daysoff.model.DaysOffEvent;
import amhamogus.com.daysoff.model.EventCollection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACCOUNT_NAME = "param1";
    private String accountName;
    private EventCollection events;

    private OnFragmentInteractionListener mListener;

    GoogleAccountCredential mCredential;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String ARG_CALENDAR_NAME = "calendarName";
    private static final String ARG_EVENT_LIST = "eventList";
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public EventDetailFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Parameter 1.
     * @return A new instance of fragment EventDetailFragment.
     */
    public static EventDetailFragment newInstance(String name, EventCollection eventList) {
        EventDetailFragment fragment = new EventDetailFragment();

        if (name != null) {
            Bundle args = new Bundle();
            args.putString(ARG_ACCOUNT_NAME, name);
            args.putParcelable(ARG_EVENT_LIST, eventList);
            fragment.setArguments(args);
        } else {
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            accountName = getArguments().getString(ARG_ACCOUNT_NAME);
            events = getArguments().getParcelable(ARG_EVENT_LIST);
        }

        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(accountName)
                .setBackOff(new ExponentialBackOff());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.list_events, container, false);

        CardView card = (CardView) rootView.findViewById(R.id.emptyEventList);
        RecyclerView view = (RecyclerView) rootView.findViewById(R.id.event_list);

        if (events.getEvents().size() > 0) {

            // Hide user call to action
            card.setVisibility(View.INVISIBLE);

            // Populate the recycler view with events
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(getActivity().getApplicationContext());
            view.setLayoutManager(layoutManager);
            view.addItemDecoration(new DividerItemDecoration(getContext()));
            view.setAdapter(new EventsRecyclerViewAdapter(events.getEvents()));
        } else {
            // No events found. Show a call to action
            // for users to create an event
            view.setVisibility(View.INVISIBLE);
        }

        //new getEventsTask(mCredential).execute();

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};

        private Drawable divider;

        public DividerItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(canvas);
            }
        }
    }

    private class getEventsTask extends AsyncTask<Void, Void, String> {

        private com.google.api.services.calendar.Calendar eventService = null;
        private Exception mLastError = null;

        public getEventsTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            eventService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off - Debug")
                    .build();
        }

        @Override
        protected String doInBackground(Void... params) {
            String eventList = "";
            try {
                getEvents();
            } catch (IOException io) {
            }
            return "";
        }

        @NonNull
        private String getEvents() throws IOException {

            DateTime date = new DateTime(System.currentTimeMillis());

            Events events = eventService.events()
                    .list("primary")
                    .setTimeMin(date)
                    .execute();

//            for (Event event : events.getItems()) {
//
//            }
            return "";
        }

        @Override
        protected void onPreExecute() {
            //Empty for now
        }

        @Override
        protected void onPostExecute(String output) {
            //Empty for now
        }
    }
}
