package amhamogus.com.daysoff.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import amhamogus.com.daysoff.R;

/**
 * Created by amhamogus on 11/25/16.
 */

public class ContactsAdapter extends ArrayAdapter<String> {

    ArrayList<String> mList;

    public ContactsAdapter(Context context, ArrayList<String> data) {
        super(context, 0, data);
        mList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.row_contact_item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.contact_name);
        name.setText(mList.get(position));
        return convertView;
    }
}
