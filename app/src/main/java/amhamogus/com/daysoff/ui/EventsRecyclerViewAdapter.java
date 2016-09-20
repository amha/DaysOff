package amhamogus.com.daysoff.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import amhamogus.com.daysoff.R;

/**
 * Created by amhamogus on 9/15/16.
 */
public class EventsRecyclerViewAdapter extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> dataModel;

    public EventsRecyclerViewAdapter(ArrayList<String> data) {
        dataModel = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_event_item, parent, false);
        return new ViewHolder(rootview);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mTextView.setText(dataModel.get(position));
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextView;

        public ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.event_title);
        }
    }
}
