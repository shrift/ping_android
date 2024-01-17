package com.bubbletastic.android.ping.userinterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.bubbletastic.android.ping.R;


/**
 * An activity representing a single Host detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link HostListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link HostDetailFragment}.
 */
public class HostDetailActivity extends AppCompatActivity {

    private String hostName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_detail);

        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            hostName = getIntent().getStringExtra(HostDetailFragment.HOST_KEY);
            arguments.putString(HostDetailFragment.HOST_KEY, hostName);
            HostDetailFragment fragment = new HostDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.host_detail_container, fragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.host_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            Intent settingsIntent = new Intent(this, HostSettingsActivity.class);
            settingsIntent.putExtra(HostDetailFragment.HOST_KEY, hostName);
            startActivity(settingsIntent);
            return true;
        } else if (itemId == android.R.id.home) {
            navigateUpTo(new Intent(this, HostListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
