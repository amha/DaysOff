package amhamogus.com.daysoff.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.ui.CalendarItemFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

/**
 *  Add description.
 */
public class CalendarItemRecyclerViewAdapter
        extends RecyclerView.Adapter<CalendarItemRecyclerViewAdapter.ViewHolder> {

    /**
     * A collection of...
     */
    private final ArrayList<String> mValues;
    private final ArrayList<String> calendarID;

    /**
     * Callback that.....
     */
    private final OnListFragmentInteractionListener mListener;

    public CalendarItemRecyclerViewAdapter(ArrayList<String> items, ArrayList<String> ids,
                                           OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        calendarID = ids;
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
        holder.mIdView.setText(position + "");
        holder.mContentView.setText(mValues.get(position));
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
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public String mItem;

        public ViewHolder(View view) {
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
