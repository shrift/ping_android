package com.bubbletastic.android.ping;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.TimeUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
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
        TextView hostName;
        TextView updated;

        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = mInflater.inflate(R.layout.host_item_list, parent, false);
        } else {
            view = convertView;
        }

        Host host = (Host) getItem(position);
        hostName = (TextView) view.findViewById(R.id.host_item_list_hostname);
        hostName.setText(host.toString());

        updated = (TextView) view.findViewById(R.id.host_item_list_updated);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -1);
        Date oneMinuteAgo = cal.getTime();
        if (host.getRefreshed() == null || host.getRefreshed().before(oneMinuteAgo)) {
            new CheckIsReachable(host, updated).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            updateHostStatusInfo(updated, host);
        }

        return view;
    }

    public Context getContext() {
        return context;
    }

    private class CheckIsReachable extends AsyncTask<Void, Void, HostStatus> {

        private long updateStarted;
        private TextView updateView;
        private Host host;
        private boolean reachable;

        public CheckIsReachable(Host host, TextView updateView) {
            this.host = host;
            this.updateView = updateView;
        }

        @Override
        protected void onPreExecute() {
            updateView.setTag(host.getHostName());
            updateView.setText("updating...");
            updateStarted = System.currentTimeMillis();
        }

        @Override
        protected HostStatus doInBackground(Void... voidness) {
            HostStatus status = HostStatus.unknown;

            try {
                InetAddress address = InetAddress.getByName(host.getHostName());
                reachable = address.isReachable(3000);
                status = HostStatus.reachable;
            } catch (UnknownHostException e) {
                System.err.println("Unknown host " + host.getHostName());
                status = HostStatus.unreachable;
            } catch (IOException e) {
                System.err.println("Unable to reach " + host.getHostName());
                status = HostStatus.unreachable;
            }

            return status;
        }

        @Override
        protected void onPostExecute(HostStatus status) {

            if (updateView.getTag().equals(host.getHostName())) {
                host.setRefreshed(new Date());
                host.setStatus(status);

                updateHostStatusInfo(updateView, host);
            }
        }
    }

    private void updateHostStatusInfo(TextView updateView, Host host) {
        String timeAgo = TimeUtil.getTimeAgo(host.getRefreshed().getTime());
        if (timeAgo != null && !timeAgo.trim().isEmpty()) {
            updateView.setText(timeAgo);
        }

        View backgroundView = ((View) updateView.getParent());
        switch (host.getStatus()) {
            case unreachable:
                backgroundView.setBackgroundResource(R.drawable.list_selector_host_unreachable);
                break;
            case reachable:
                backgroundView.setBackgroundResource(R.drawable.list_selector_host_reachable);
                break;
            default:
                backgroundView.setBackgroundResource(R.drawable.list_selector_host_unknown);
                break;

        }
    }
}
