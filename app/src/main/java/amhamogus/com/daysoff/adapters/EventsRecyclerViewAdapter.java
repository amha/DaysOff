package amhamogus.com.daysoff.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import amhamogus.com.daysoff.R;
import amhamogus.com.daysoff.model.DaysOffEvent;

public class EventsRecyclerViewAdapter
        extends RecyclerView.Adapter<EventsRecyclerViewAdapter.ViewHolder> {

    private List<DaysOffEvent> dataModel;

    public EventsRecyclerViewAdapter(List<DaysOffEvent> data) {
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
        holder.mTextView.setText(dataModel.get(position).getEventSummary());
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView mTextView;

        ViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.event_title);
        }
    }
}
