package com.bubbletastic.android.ping;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bubbletastic.android.ping.view.EditTextBackEvent;
import com.bubbletastic.android.ping.view.EditTextImeBackListener;
import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HostListFragment extends Fragment implements EditTextImeBackListener, BackPressedListener {

    private static final String prefKeyHosts = "saved_hosts";
    private static final String prefsName = "app_preferences";
    private SharedPreferences prefs;

    private List<Host> hosts;

    private ListView listView;
    private int listViewPaddingBottom;
    private HostAdapter adapter;

    private FloatingActionButton addHostFab;
    private View headerAddInputView;
    private EditTextBackEvent addHostInput;
    private SwipeRefreshLayout swipeRefreshLayout;

    public HostListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getActivity().getSharedPreferences(prefsName, Context.MODE_PRIVATE);

        //Initialize empty host list to avoid null issues.
        hosts = new ArrayList<Host>();

        retrievePersistedHosts();
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
        adapter = createAdapter();
        listView.setAdapter(adapter);

        if (hosts == null || hosts.isEmpty()) {
            showAddHostInput();
        }

        return view;
    }

    private void refreshHosts() {

        for (Host host : hosts) {
            host.setRefreshed(null);
        }

        adapter.notifyDataSetInvalidated();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 250);
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

    private void removeHosts(List<Host> hostsForRemoval) {
        hosts.removeAll(hostsForRemoval);
        Collections.sort(hosts);
        adapter.notifyDataSetChanged();
        persistHosts();

        if (hosts == null || hosts.isEmpty()) {
            showAddHostInput();
        }
    }

    private void addHost(String hostName) {
        Host host = new Host(hostName);
        if (!hosts.contains(host)) {
            hosts.add(host);
            Collections.sort(hosts);
            adapter.notifyDataSetChanged();
            persistHosts();

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

    private List<Host> retrievePersistedHosts() {
        String hostsJson = prefs.getString(prefKeyHosts, null);
        if (hostsJson != null) {
            Gson gson = new Gson();
            Type hostsType = new TypeToken<ArrayList<Host>>() {
            }.getType();
            hosts = gson.fromJson(hostsJson, hostsType);
            Collections.sort(hosts);
            return hosts;
        }
        return null;
    }

    private void persistHosts() {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = prefs.edit();
        Type hostsType = new TypeToken<ArrayList<Host>>() {
        }.getType();
        editor.putString(prefKeyHosts, gson.toJson(hosts, hostsType));

        editor.apply();
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
