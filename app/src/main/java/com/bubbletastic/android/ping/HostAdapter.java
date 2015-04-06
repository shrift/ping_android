package com.bubbletastic.android.ping;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by brendanmartens on 4/5/15.
 */
public class HostAdapter extends ArrayAdapter {

    public HostAdapter(Context context, Host[] hosts) {
        super(context, android.R.layout.simple_list_item_activated_1, android.R.id.text1, hosts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextView text;

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = mInflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
        } else {
            view = convertView;
        }

        try {
            int mFieldId = android.R.id.text1;
            if (mFieldId == 0) {
                //  If no custom field is assigned, assume the whole resource is a TextView
                text = (TextView) view;
            } else {
                //  Otherwise, find the TextView field within the layout
                text = (TextView) view.findViewById(mFieldId);
            }
        } catch (ClassCastException e) {
            Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
            throw new IllegalStateException(
                    "ArrayAdapter requires the resource ID to be a TextView", e);
        }

        Host host = (Host) getItem(position);
        if (host instanceof CharSequence) {
            text.setText((CharSequence)host);
        } else {
            text.setText(host.toString());
        }

        return view;
    }
}
