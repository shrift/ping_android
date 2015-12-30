package com.bubbletastic.android.ping.userinterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bubbletastic.android.ping.R;

/**
 * Created by brendanmartens on 12/19/15.
 * <p/>
 * Note that this does not extend the PreferenceActivity as that activity is no longer recommended.
 * Instead, to easily achieve the action bar behavior desired, just wrap the PreferenceFragment in this standard activity.
 */
public class HostSettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host_activity_settings);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            String hostName = getIntent().getStringExtra(HostDetailFragment.HOST_KEY);
            arguments.putString(HostDetailFragment.HOST_KEY, hostName);
            HostSettingsFragment fragment = new HostSettingsFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction().add(R.id.host_settings_container, fragment).commit();

            setTitle(hostName + " " + getString(R.string.title_host_settings_activity));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //This activity should only be navigable by the settings icon on a host detail activity, so just finish it to go back there.
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
