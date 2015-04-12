package com.bubbletastic.android.ping;

import android.app.Activity;
import android.os.Bundle;

public class HostListActivity extends Activity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private HostListFragment hostListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_list);

        if (findViewById(R.id.host_detail_container) != null) {
            mTwoPane = true;
        }

        hostListFragment = (HostListFragment) getFragmentManager().findFragmentById(R.id.host_list);
    }

    @Override
    public void onBackPressed() {
        boolean handled = hostListFragment.backPressed();
        if (!handled) {
            super.onBackPressed();
        }
    }
}
