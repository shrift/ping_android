package com.bubbletastic.android.ping;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HostListFragment extends Fragment {

    private ListView listView;
    private List<Host> hosts;
    private HostAdapter adapter;
    private View headerAddInputView;

    public HostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hosts = new ArrayList<Host>(Arrays.asList(new Host("a;lkajsdf;lkjsadfjk"), new Host("snarkletastic.com"), new Host("bubbletastic.com"), new Host("brendanmartens.com"), new Host("8.8.8.8"), new Host("a;lkajsdf;lkjsadfjk"), new Host("snarkletastic.com"), new Host("bubbletastic.com"), new Host("brendanmartens.com"), new Host("8.8.8.8"), new Host("a;lkajsdf;lkjsadfjk"), new Host("snarkletastic.com"), new Host("bubbletastic.com"), new Host("brendanmartens.com"), new Host("8.8.8.8"), new Host("a;lkajsdf;lkjsadfjk"), new Host("snarkletastic.com"), new Host("bubbletastic.com"), new Host("brendanmartens.com"), new Host("8.8.8.8")));
        Collections.sort(hosts);
    }

    private HostAdapter createAdapter() {
        return new HostAdapter(getActivity(), hosts);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_list, container, false);

        FloatingActionButton addHost = (FloatingActionButton) view.findViewById(R.id.fragment_host_list_add_fab);
        addHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHostInput();
            }
        });


        listView = (ListView) view.findViewById(R.id.list);
        adapter = createAdapter();
        listView.setAdapter(adapter);

//        EditText addHost = (EditText) view.findViewById(R.id.fragment_host_list_add);
//        addHost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                hosts.add(new Host(v.getText().toString()));
//                Collections.sort(hosts);
//                adapter.notifyDataSetChanged();
//                return true;
//            }
//
//        });
        return view;
    }

    private void showAddHostInput() {
        if (headerAddInputView == null) {
            LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            headerAddInputView = mInflater.inflate(R.layout.host_list_add, listView, false);
            listView.addFooterView(headerAddInputView);
        }
        listView.setSelection(adapter.getCount() - 1);
    }


}
