package com.bubbletastic.android.ping.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.bubbletastic.android.ping.R;

public class HostListActivity extends AppCompatActivity implements HostListCallbacks {

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

    /**
     * Callback method from {@link HostListCallbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(HostDetailFragment.HOST_KEY, id);
            HostDetailFragment fragment = new HostDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.host_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, HostDetailActivity.class);
            detailIntent.putExtra(HostDetailFragment.HOST_KEY, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.host_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            Intent detailIntent = new Intent(this, SettingsActivity.class);
            startActivity(detailIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
