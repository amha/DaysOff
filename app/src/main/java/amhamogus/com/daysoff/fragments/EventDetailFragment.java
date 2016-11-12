package amhamogus.com.daysoff.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.adapters.EventsRecyclerViewAdapter;
import amhamogus.com.daysoff.model.DaysOffEvent;
import amhamogus.com.daysoff.model.EventCollection;
import amhamogus.com.daysoff.utils.CollectionHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventDetailFragment.OnEventSelected} interface
 * to handle interaction events.
 * Use the {@link EventDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailFragment extends Fragment {

    private static final String TAG = "EVENT DETAIL";
    private static final String ARG_CURRENT_DATE = "currentDate";

    private static final String PREF_FILE = "calendarSessionData";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_CALENDAR_ID = "calendarId";

    private String accountName;
    private String calendarId;
    private Date currentDate;
    private EventCollection events;
    private List<DaysOffEvent> eventsOnASelectedDate;
    View rootView;

    RecyclerView view;
    CardView card;

    private OnEventSelected mListener;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    public EventDetailFragment() {
    }

    /**
     * Creates an instance of {@link android.app.Fragment} that
     * fetches a list of {@link Events} from the backend.
     *
     * @param selectedDate User selected date
     * @return A fragment that represents the details of an event
     */
    public static EventDetailFragment newInstance(long selectedDate) {

        Bundle args = new Bundle();
        args.putLong(ARG_CURRENT_DATE, selectedDate);

        EventDetailFragment fragment = new EventDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDate = new Date(getArguments().getLong(ARG_CURRENT_DATE));
        setHasOptionsMenu(true);

        SharedPreferences settings = getActivity()
                .getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        accountName = settings.getString(PREF_ACCOUNT_NAME, null);
        calendarId = settings.getString(PREF_CALENDAR_ID, null);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getContext(), Arrays.asList(SCOPES))
                .setSelectedAccountName(accountName)
                .setBackOff(new ExponentialBackOff());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.list_events, container, false);
        card = (CardView) rootView.findViewById(R.id.emptyEventList);
        card.setVisibility(View.INVISIBLE);
        view = (RecyclerView) rootView.findViewById(R.id.event_list);
        view.setVisibility(View.INVISIBLE);

        new getEventsTask(mCredential).execute();

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventSelected) {
            mListener = (OnEventSelected) context;
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_event_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share_action:
                StringBuilder message = new StringBuilder();
                message.append("Here's my schedule: ");


                if (eventsOnASelectedDate.size() > 0) {
                    for (int i = 0; i < eventsOnASelectedDate.size(); i++) {
                        message.append(eventsOnASelectedDate.get(i).getEventSummary() + ", "
                                + eventsOnASelectedDate.get(i).getTimeRange() +
                                eventsOnASelectedDate.get(i).getDesc() + ", ");
                    }
                }

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, message.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
        return true;
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
    public interface OnEventSelected {
        void onFragmentInteraction(String message, DaysOffEvent event);
    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable divider;

        DividerItemDecoration(Context context) {
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

                RecyclerView.LayoutParams params =
                        (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(canvas);
            }
        }
    }

    private class getEventsTask extends AsyncTask<Void, Void, List<Event>> {

        private com.google.api.services.calendar.Calendar eventService = null;
        private Exception mLastError = null;

        protected getEventsTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            eventService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Days Off - Debug")
                    .build();
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            try {
                return getEvents();
            } catch (IOException io) {
                //TODO
                return null;
            }
        }

        @NonNull
        private List<Event> getEvents() throws IOException {
            DateTime date = new DateTime(System.currentTimeMillis());
            Events events = eventService.events()
                    .list(calendarId)
                    .setTimeMin(date)
                    .execute();
            return events.getItems();
        }

        @Override
        protected void onPostExecute(List<Event> output) {
            if (output != null) {
                if (output.size() > 0) {
                    Date eventDate;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    events = new EventCollection(CollectionHelper.convertListToCollection(output));
                    eventsOnASelectedDate = new ArrayList<>();

                    // Get events for the user selected day
                    for (int i = 0; i < events.getEvents().size(); i++) {
                        try {
                            eventDate = format.parse(events.getEvents().get(i).getStartTime());
                            if (currentDate.compareTo(eventDate) == 0) {
                                eventsOnASelectedDate.add(events.getEvents().get(i));
                            }
                        } catch (java.text.ParseException e) {
                            // Create error handling mechanism.
                        }
                    }


                    if (eventsOnASelectedDate.size() > 0) {
                        Log.d(TAG, "event found today");
                        // Add relevant events to the recycler view
                        LinearLayoutManager layoutManager =
                                new LinearLayoutManager(getActivity().getApplicationContext());
                        view.setLayoutManager(layoutManager);
                        view.addItemDecoration(new DividerItemDecoration(getContext()));
                        view.setAdapter(new EventsRecyclerViewAdapter(eventsOnASelectedDate, mListener));

                        // show relevant views
                        view.setVisibility(View.VISIBLE);
                        card.setVisibility(View.INVISIBLE);
                    } else {
                        // display add event Call to Action
                        card.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
}
