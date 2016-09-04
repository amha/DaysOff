package amhamogus.com.daysoff.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.api.services.calendar.model.CalendarListEntry;

import amhamogus.com.daysoff.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CalendarItemFragment extends Fragment {


    /**
     * List of Calendars from Google Calendar
     */
    private ArrayList<String> mCalendarList;

    /**
     * TODO: Determine if this is still needed
     */
    private static final String ARG_COLUMN_COUNT = "columnCount";

    /**
     * The key for the list parameter
     */
    private static final String ARG_CALENDAR_LIST = "list";

    /**
     * TODO: Determine if this is still needed
     */
    private int mColumnCount = 1;

    /**
     *
     */
    private OnListFragmentInteractionListener mListener;

    private ArrayList<String> calendarID;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CalendarItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CalendarItemFragment newInstance(int columnCount,
                                                   List<CalendarListEntry> calendarList) {

        CalendarItemFragment fragment = new CalendarItemFragment();
        ArrayList<String> calendarListArray = new ArrayList<String>(calendarList.size());
        ArrayList<String> idArray = new ArrayList<>(calendarList.size());

        for (int i = 0; i < calendarList.size(); i++) {
            calendarListArray.add(calendarList.get(i).getSummary());
            idArray.add(calendarList.get(i).getId());
        }

        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putStringArrayList(ARG_CALENDAR_LIST, calendarListArray);
        args.putStringArrayList("temp", idArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  parameters instance variables
        if (getArguments() != null) {

            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mCalendarList = getArguments().getStringArrayList(ARG_CALENDAR_LIST);
            calendarID = getArguments().getStringArrayList("temp");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendaritem_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyCalendarItemRecyclerViewAdapter(mCalendarList, calendarID, mListener));
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
        // TODO: Update argument type and name
        void onListFragmentInteraction(String item);
    }
}
