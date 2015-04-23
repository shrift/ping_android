package com.bubbletastic.android.ping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by brendanmartens on 4/5/15.
 */
public class HostAdapter extends BaseAdapter {

    private List<Host> hosts;
    private Context context;

    public HostAdapter(Context context, List<Host> hosts) {
//        super(context, R.layout.host_item_list, android.R.id.text1, hosts);
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

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = mInflater.inflate(R.layout.host_item_list, parent, false);
        } else {
            view = convertView;
        }
        Host host = (Host) getItem(position);

        //reset status color
        indicator = view.findViewById(R.id.host_item_list_status_indicator);
        indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);


        hostName = (TextView) view.findViewById(R.id.host_item_list_hostname);
        hostName.setText(host.toString());

        updated = (TextView) view.findViewById(R.id.host_item_list_updated);


//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.MINUTE, -1);
//        Date oneMinuteAgo = calendar.getTime();
//        if (host.getRefreshed() == null || host.getRefreshed().before(oneMinuteAgo)) {
//            new CheckIsReachable(host, updated).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        } else {
        updateHostStatusInfo(updated, host);
//        }

        return view;
    }

    public Context getContext() {
        return context;
    }

    private void updateHostStatusInfo(TextView updateView, Host host) {
//        String timeAgo = TimeUtil.getTimeAgo(host.getRefreshed().getTime());
//        if (timeAgo != null && !timeAgo.trim().isEmpty()) {
//            updateView.setText(timeAgo);
//        }

        View indicator = ((View) updateView.getParent()).findViewById(R.id.host_item_list_status_indicator);
        switch (host.getCurrentStatus()) {
            case unreachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unreachable);
                updateView.setText(null);
                break;
            case reachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_reachable);
                updateView.setText(null);
                break;
            case updating:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
                updateView.setText(getContext().getString(R.string.host_updating));
                break;
            default:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
                updateView.setText(getContext().getString(R.string.updated_unknown));
                break;

        }
    }
}
