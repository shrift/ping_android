package com.bubbletastic.android.ping.userinterface;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bubbletastic.android.ping.model.Host;
import com.bubbletastic.android.ping.R;
import com.bubbletastic.android.ping.model.proto.PingResult;
import com.bubbletastic.android.ping.userinterface.view.HostViewHolder;
import com.bubbletastic.android.ping.userinterface.view.PingResultAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a single Host detail screen.
 * This fragment is either contained in a {@link HostListActivity}
 * in two-pane mode (on tablets) or a {@link HostDetailActivity}
 * on handsets.
 */
public class HostDetailFragment extends PingFragment {
    /**
     * The fragment argument representing the host ID that this fragment
     * represents.
     */
    public static final String HOST_KEY = "host_key";

    /**
     * The dummy content this fragment is presenting.
     */
    private Host host;
    private PingResultAdapter adapter;
    private HostViewHolder hostViewHolder;
    private List<PingResult> results;
    private Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HostDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(HOST_KEY)) {
            String hostName = getArguments().getString(HOST_KEY);
            host = getApp().getHostService().retrievePersistedHost(hostName);
        }

        getActivity().setTitle(host.getHostName());

        handler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApp().getBus().unregister(this);
    }

    @Subscribe
    public void hostUpdated(final Host host) {
        if (!host.equals(this.host)) {
            return;
        }
        this.host = host;

        //the bus may be delivering events from a different thread, so post to main thread handler
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    return;
                }
                results.clear();
                results.addAll(host.getResults());
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_host_detail, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_host_detail_swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHost();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        hostViewHolder = new HostViewHolder();
        hostViewHolder.indicator = rootView.findViewById(R.id.host_item_list_status_indicator);
        hostViewHolder.hostName = (TextView) rootView.findViewById(R.id.host_item_list_hostname);
        hostViewHolder.updated = (TextView) rootView.findViewById(R.id.host_item_list_updated);

        updateHostHeader();

        ListView listView = (ListView) rootView.findViewById(R.id.list);
        results = new ArrayList<PingResult>(host.getResults());
        adapter = new PingResultAdapter(getActivity().getApplicationContext(), results);
        listView.setAdapter(adapter);

        //don't register for host updates until our views have been configured
        getApp().getBus().register(this);

        return rootView;
    }

    private void updateHostHeader() {
        hostViewHolder.updated.setVisibility(View.GONE);
        hostViewHolder.hostName.setText(host.toString());
        hostViewHolder.updateHostStatusInfo(host);
    }

    private void refreshHost() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getApp().getHostService().refreshHost(host);
            }
        }).start();
    }
}
