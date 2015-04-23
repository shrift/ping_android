package com.bubbletastic.android.ping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bubbletastic.android.ping.view.HostViewHolder;
import com.bubbletastic.android.ping.view.PingResultAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_host_detail, container, false);

        View indicator = rootView.findViewById(R.id.host_item_list_status_indicator);
        TextView hostName = (TextView) rootView.findViewById(R.id.host_item_list_hostname);
        TextView updated = (TextView) rootView.findViewById(R.id.host_item_list_updated);

        HostViewHolder hostViewHolder = new HostViewHolder();
        hostViewHolder.indicator = indicator;
        hostViewHolder.hostName = hostName;
        hostViewHolder.updated = updated;

        hostName.setText(host.toString());
        hostViewHolder.updateHostStatusInfo(host);
        updated.setVisibility(View.GONE);

        listView = (ListView) rootView.findViewById(R.id.list);
        adapter = new PingResultAdapter(getActivity().getApplicationContext(), host.getResults());
        listView.setAdapter(adapter);

        return rootView;
    }
}
