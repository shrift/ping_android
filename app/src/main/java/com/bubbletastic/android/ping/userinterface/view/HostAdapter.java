package com.bubbletastic.android.ping.userinterface.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bubbletastic.android.ping.Host;
import com.bubbletastic.android.ping.R;

import java.util.List;

/**
 * Created by brendanmartens on 4/5/15.
 */
public class HostAdapter extends BaseAdapter {

    private List<Host> hosts;
    private Context context;

    public HostAdapter(Context context, List<Host> hosts) {
        this.hosts = hosts;
        this.context = context;
    }

    public int getPositionOfHost(Host host) {
        return hosts.indexOf(host);
    }

    @Override
    public int getCount() {
        if (hosts == null) {
            return 0;
        }
        return hosts.size();
    }

    @Override
    public Host getItem(int position) {
        return hosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        View indicator;
        TextView hostName;
        TextView updated;
        HostViewHolder hostViewHolder;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = mInflater.inflate(R.layout.host_list_item, parent, false);
            indicator = view.findViewById(R.id.host_item_list_status_indicator);
            hostName = (TextView) view.findViewById(R.id.host_item_list_hostname);
            updated = (TextView) view.findViewById(R.id.host_item_list_updated);
            hostViewHolder = new HostViewHolder();
            hostViewHolder.indicator = indicator;
            hostViewHolder.hostName = hostName;
            hostViewHolder.updated = updated;
            view.setTag(hostViewHolder);
        } else {
            view = convertView;
            hostViewHolder = (HostViewHolder) view.getTag();
            indicator = hostViewHolder.indicator;
            hostName = hostViewHolder.hostName;
            updated = hostViewHolder.updated;
        }
        Host host = (Host) getItem(position);

        indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
        hostName.setText(host.toString());

        hostViewHolder.updateHostStatusInfo(host);

        return view;
    }
}
