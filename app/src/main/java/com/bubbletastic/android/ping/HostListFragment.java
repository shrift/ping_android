package com.bubbletastic.android.ping;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bubbletastic.android.ping.view.EditTextBackEvent;
import com.bubbletastic.android.ping.view.EditTextImeBackListener;
import com.bubbletastic.android.ping.view.HostAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HostListFragment extends PingFragment implements EditTextImeBackListener, BackPressedListener {

    /**
     * A dummy implementation of the {@link HostListCallbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static HostListCallbacks dummyCallbacks = new HostListCallbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private HostListCallbacks callbacks = dummyCallbacks;

    private List<Host> hosts;

    private ListView listView;
    private int listViewPaddingBottom;
    private HostAdapter adapter;

    private FloatingActionButton addHostFab;
    private View headerAddInputView;
    private EditTextBackEvent addHostInput;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;


    public HostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        getApp().getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getApp().getBus().unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof HostListCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        callbacks = (HostListCallbacks) activity;
    }

    @Subscribe
    public void hostsUpdating(final HostsUpdating hostsUpdating) {
        //the bus may be delivering events from a different thread, so post to main thread handler
        if (hostsUpdating.isUpdating()) {
            //oddly, we only want to do anything if this is false
            //(such as hide the swipe-refresh refreshing indicator as it should not be shown without explicit user interaction)
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(hostsUpdating.isUpdating());
            }
        });
    }

    @Subscribe
    public void hostUpdated(final Host host) {
        //the bus may be delivering events from a different thread, so post to main thread handler
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (adapter == null) {
                    return;
                }
                if (hosts.contains(host)) {
                    hosts.remove(host);
                    hosts.add(host);
                    Collections.sort(hosts);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private HostAdapter createAdapter() {
        return new HostAdapter(getActivity(), hosts);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_host_list_swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshHosts();
            }
        });

        addHostFab = (FloatingActionButton) view.findViewById(R.id.fragment_host_list_add_fab);
        addHostFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHostInput();
            }
        });

        headerAddInputView = view.findViewById(R.id.host_list_add);
        ImageButton addHostButton = (ImageButton) headerAddInputView.findViewById(R.id.host_list_add_button_add);
        addHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHost(addHostInput.getText().toString());
                scrollAddHostInputIntoView();
            }
        });
        ImageButton doneAddingHostsButton = (ImageButton) headerAddInputView.findViewById(R.id.host_list_add_button_done);
        doneAddingHostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHost(addHostInput.getText().toString());
                dismissAddHostInput();
                dismissSoftKeyboard();
            }
        });


        addHostInput = (EditTextBackEvent) headerAddInputView.findViewById(R.id.host_list_add_input);
        addHostInput.setOnEditTextImeBackListener(this);
//        addHostInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (v.hasFocus()) {
//                    imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
//                } else {
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        });
        addHostInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addHost(v.getText().toString());
                dismissAddHostInput();
                dismissSoftKeyboard();
                return false;
            }

        });

        listView = (ListView) view.findViewById(R.id.list);
        listViewPaddingBottom = listView.getPaddingBottom();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callbacks.onItemSelected(adapter.getItem(position).getHostName());
            }
        });
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                  long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        deleteSelectedHosts();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.host_list_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });

        //Initialize empty host list to avoid null issues.
        hosts = new ArrayList<Host>();

        adapter = createAdapter();
        listView.setAdapter(adapter);


        retrievePersistedHosts();
        return view;
    }

    private void refreshHosts() {
        getApp().refreshHostsSoon();
        swipeRefreshLayout.setRefreshing(true);
    }

    private void deleteSelectedHosts() {
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        List<Host> hostsForRemoval = new ArrayList<Host>();
        for (int i = 0; i < checked.size(); i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value) {
                Host host = adapter.getItem(key);
                hostsForRemoval.add(host);
                Log.d("remove host", host.toString());
            }
        }
        removeHosts(hostsForRemoval);
    }

    @Override
    public void onImeBack() {
        //keyboard has been hidden from the edit text host input.
        dismissAddHostInput();
    }

    private void showAddHostInput() {
        addAddInputView();
        scrollAddHostInputIntoView();
        addHostFab.setVisibility(View.GONE);
        listView.setPadding(
                listView.getPaddingLeft(),
                listView.getPaddingTop(),
                listView.getPaddingRight(),
                0);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
        //request focus shortly after showing the add host input.
//                addHostInput.requestFocus();
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(addHostFab, InputMethodManager.SHOW_IMPLICIT);
//            }
//        }, 50);
    }

    private void scrollAddHostInputIntoView() {
        listView.setSelection(adapter.getCount() - 1);
    }

    private void removeHosts(final List<Host> hostsForRemoval) {
        hosts.removeAll(hostsForRemoval);
        Collections.sort(hosts);
        adapter.notifyDataSetChanged();
//        persistHostsOverwrite();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getApp().getHostService().removeHosts(hostsForRemoval);
            }
        }).start();

        if (hosts.isEmpty()) {
            showAddHostInput();
        }
    }

    private void addHost(String hostName) {
        if (hostName == null || hostName.trim().isEmpty()) {
            return;
        }

        final Host host = new Host(hostName);
        if (!hosts.contains(host)) {
            hosts.add(host);
            Collections.sort(hosts);
            adapter.notifyDataSetChanged();
            persistHosts();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    getApp().getHostService().refreshHost(host);
                }
            }).start();

            listView.smoothScrollToPosition(adapter.getPositionOfHost(host));
        }
    }

    private void dismissSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addHostInput.getWindowToken(), 0);
    }

    private void dismissAddHostInput() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //post slightly delayed so the keyboard goes away first (if it was open).
                addHostFab.setVisibility(View.VISIBLE);
                listView.setPadding(
                        listView.getPaddingLeft(),
                        listView.getPaddingTop(),
                        listView.getPaddingRight(),
                        listViewPaddingBottom);
                removeAddInputView();
            }
        }, 50);
    }

    /**
     * Abstracted so that logic is not tied to where in the layout the input view is.
     */
    private void removeAddInputView() {
//        listView.removeFooterView(headerAddInputView);
        headerAddInputView.setVisibility(View.GONE);
    }

    /**
     * Abstracted so that logic is not tied to where in the layout the input view is.
     */
    private void addAddInputView() {
//        listView.addFooterView(headerAddInputView);
        headerAddInputView.setVisibility(View.VISIBLE);
    }

    private void retrievePersistedHosts() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //retain this hosts list reference so we don't have to recreate the adapter, or reset its hosts list.
                hosts.clear();
                hosts.addAll(getApp().getHostService().retrievePersistedHosts());
                Collections.sort(hosts);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetInvalidated();
                if (hosts.isEmpty()) {
                    showAddHostInput();
                }
            }
        }.execute();
    }

    private void persistHosts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //copy the list here to ensure we don't have any concurrent modification exeptions with this list getting edited while being written out to storage
                getApp().getHostService().persistHostsOverwrite(new ArrayList<Host>(hosts));
            }
        }).start();
    }


    @Override
    public boolean backPressed() {
        if (headerAddInputView.getVisibility() == View.VISIBLE) {
            dismissAddHostInput();
            return true;
        }
        return false;
    }
}
