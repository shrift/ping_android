package com.bubbletastic.android.ping;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bubbletastic.android.ping.model.proto.PingResult;
import com.bubbletastic.android.ping.view.HostViewHolder;
import com.bubbletastic.android.ping.view.PingResultAdapter;
import com.squareup.otto.Subscribe;

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
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Host host;
    private ListView listView;
    private PingResultAdapter adapter;
    private HostViewHolder hostViewHolder;
    private List<PingResult> results;
    private Handler handler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HostDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            String hostName = getArguments().getString(ARG_ITEM_ID);
            host = getApp().getHostService().retrievePersistedHost(hostName);
        }

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
        //the bus may be delivering events from a different thread, so post to main thread handler
        handler.post(new Runnable() {
            @Override
            public void run() {
                refreshResults();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_host_detail, container, false);

        hostViewHolder = new HostViewHolder();
        hostViewHolder.indicator = rootView.findViewById(R.id.host_item_list_status_indicator);
        hostViewHolder.hostName = (TextView) rootView.findViewById(R.id.host_item_list_hostname);
        hostViewHolder.updated = (TextView) rootView.findViewById(R.id.host_item_list_updated);

        updateHostHeader();

        listView = (ListView) rootView.findViewById(R.id.list);
        results = host.getResults();
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

    private void refreshResults() {
        if (adapter == null) {
            return;
        }
        results.clear();
        results.addAll(host.getResults());
        adapter.notifyDataSetChanged();
    }
}
