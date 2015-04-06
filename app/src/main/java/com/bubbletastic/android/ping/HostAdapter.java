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
        return hosts.size();
    }

    @Override
    public Object getItem(int position) {
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

        new CheckIsReachable(host.hostName, updated).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    public Context getContext() {
        return context;
    }

    private class CheckIsReachable extends AsyncTask<Void, Void, HostStatus> {

        private long updateStarted;
        private TextView updateView;
        private String hostName;
        private boolean reachable;

        public CheckIsReachable(String hostName, TextView updateView) {
            this.hostName = hostName;
            this.updateView = updateView;
        }

        @Override
        protected void onPreExecute() {
            updateView.setTag(hostName);
            updateView.setText("updating...");
            updateStarted = System.currentTimeMillis();
        }

        @Override
        protected HostStatus doInBackground(Void... voidness) {
            HostStatus status = HostStatus.unknown;

            try {
                InetAddress address = InetAddress.getByName(hostName);
                System.out.println("Name: " + address.getHostName());
                System.out.println("Addr: " + address.getHostAddress());
                reachable = address.isReachable(3000);
                System.out.println("Reach: " + reachable);
                status = HostStatus.reachable;
            } catch (UnknownHostException e) {
                System.err.println("Unknown host " + hostName);
                status = HostStatus.unreachable;
            } catch (IOException e) {
                System.err.println("Unable to reach " + hostName);
                status = HostStatus.unreachable;
            }

            return status;
        }

        @Override
        protected void onPostExecute(HostStatus status) {
            String timeAgo = TimeUtil.getTimeAgo(updateStarted);
            if (timeAgo != null && !timeAgo.trim().isEmpty()) {
                updateView.setText(timeAgo);
            }

            if (updateView.getTag().equals(hostName)) {
                View view = ((View) updateView.getParent());
                switch (status) {
                    case unreachable:
                        view.setBackgroundColor(getContext().getResources().getColor(R.color.md_red_200));
                        break;
                    case reachable:
                        view.setBackgroundColor(getContext().getResources().getColor(R.color.md_green_200));
                        break;
                    default:
                        view.setBackgroundColor(getContext().getResources().getColor(R.color.md_orange_200));
                        break;

                }
            }
        }
    }
}
