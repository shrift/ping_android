package com.bubbletastic.android.ping;

import android.app.Fragment;

/**
 * Created by brendanmartens on 4/13/15.
 */
public class PingFragment extends Fragment {

    protected Ping getApp() {
        return (Ping) getActivity().getApplicationContext();
    }
}
