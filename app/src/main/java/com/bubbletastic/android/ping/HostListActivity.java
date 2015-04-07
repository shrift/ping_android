package com.bubbletastic.android.ping;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostListActivity extends Activity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_list);

        if (findViewById(R.id.host_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
//            ((HostListFragment) getFragmentManager()
//                    .findFragmentById(R.id.host_list))
//                    .setActivateOnItemClick(true);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    InetAddress address = InetAddress.getByName("bubbletastic.com");
                    System.out.println("Name: " + address.getHostName());
                    System.out.println("Addr: " + address.getHostAddress());
                    System.out.println("Reach: " + address.isReachable(3000));
                } catch (UnknownHostException e) {
                    System.err.println("Unable to lookup bubbletastic.com");
                } catch (IOException e) {
                    System.err.println("Unable to reach bubbletastic.com");
                }
            }
        }).start();
    }

    /**
     * Callback method from {@link HostListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
//    @Override
//    public void onItemSelected(String id) {
//        if (mTwoPane) {
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putString(HostDetailFragment.ARG_ITEM_ID, id);
//            HostDetailFragment fragment = new HostDetailFragment();
//            fragment.setArguments(arguments);
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.host_detail_container, fragment)
//                    .commit();
//
//        } else {
//            // In single-pane mode, simply start the detail activity
//            // for the selected item ID.
//            Intent detailIntent = new Intent(this, HostDetailActivity.class);
//            detailIntent.putExtra(HostDetailFragment.ARG_ITEM_ID, id);
//            startActivity(detailIntent);
//        }
//    }
}
