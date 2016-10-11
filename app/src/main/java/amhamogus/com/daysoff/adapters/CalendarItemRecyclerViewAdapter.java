package amhamogus.com.daysoff.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.ArrayList;
import java.util.List;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.fragments.MainListFragment.OnListFragmentInteractionListener;

/**
 * Add description.
 */
public class CalendarItemRecyclerViewAdapter
        extends RecyclerView.Adapter<CalendarItemRecyclerViewAdapter.ViewHolder> {

    /**
     * A collection of...
     */
    private final ArrayList<String> mValues;
    private final ArrayList<String> calendarID;
    private List<CalendarListEntry> calendarListEntries;

    /**
     * Callback that.....
     */
    private final OnListFragmentInteractionListener mListener;

    public CalendarItemRecyclerViewAdapter(List<CalendarListEntry> entryList,
                                           OnListFragmentInteractionListener listener) {

        mValues = new ArrayList<>(entryList.size());
        calendarID = new ArrayList<>(entryList.size());
        mListener = listener;
        calendarListEntries = entryList;

        for (int i = 0; i < entryList.size(); i++) {
            mValues.add(entryList.get(i).getSummary());
            calendarID.add(entryList.get(i).getId());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_calendar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(Integer.toString(position));
        holder.mContentView.setText(mValues.get(position));
//        holder.mContentView.setTextColor(Integer.valueOf(calendarListEntries.get(position).getBackgroundColor(), 16).intValue());
//        Log.d("AMHA-COlor", "color:" + Integer.valueOf(calendarListEntries.get(position).getBackgroundColor().substring(1), 16).intValue());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCalendarSelectedInteraction(calendarID.get(position), mValues.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        String mItem;
        public String color;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
