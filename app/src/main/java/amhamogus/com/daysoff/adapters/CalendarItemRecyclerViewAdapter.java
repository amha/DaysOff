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
 * An adapter that bind's {@link com.google.api.services.calendar.model.CalendarList} to
 * a {@link RecyclerView}.
 */
public class CalendarItemRecyclerViewAdapter
        extends RecyclerView.Adapter<CalendarItemRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> mValues;
    private final ArrayList<String> calendarID;
    private final OnListFragmentInteractionListener mListener;

    public CalendarItemRecyclerViewAdapter(List<CalendarListEntry> entryList,
                                           OnListFragmentInteractionListener listener) {

        mValues = new ArrayList<>(entryList.size());
        calendarID = new ArrayList<>(entryList.size());
        mListener = listener;

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
        final View mView;
        final TextView mContentView;
        public String color;
        String mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '"
                    + mContentView.getText() + "'";
        }
    }
}
