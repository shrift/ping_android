package com.bubbletastic.android.ping.userinterface;

import android.app.Fragment;

import com.bubbletastic.android.ping.Ping;

/**
 * Created by brendanmartens on 4/13/15.
 */
public class PingFragment extends Fragment {

    protected Ping getApp() {
        return (Ping) getActivity().getApplicationContext();
    }
}
