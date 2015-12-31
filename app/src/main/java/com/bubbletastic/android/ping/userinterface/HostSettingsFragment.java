package com.bubbletastic.android.ping.userinterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.bubbletastic.android.ping.model.Host;
import com.bubbletastic.android.ping.Ping;
import com.bubbletastic.android.ping.R;

/**
 * Created by brendanmartens on 12/18/15.
 */
public class HostSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreference showNotificationsPreference;
    private Host host;
    private Ping ping;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ping = (Ping) getActivity().getApplicationContext();
        if (getArguments().containsKey(HostDetailFragment.HOST_KEY)) {
            String hostName = getArguments().getString(HostDetailFragment.HOST_KEY);
            host = ping.getHostService().retrievePersistedHost(hostName);
        }
        
        addPreferencesFromResource(R.xml.host_preferences);

        showNotificationsPreference = (SwitchPreference) findPreference(getString(R.string.pref_key_host_show_notification));

        showNotificationsPreference.setChecked(host.isShowNotification());
    }

    @Override
    public void onResume() {
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_host_show_notification))) {
            toggleShowHostNotification();
        }
    }

    private void toggleShowHostNotification() {
        host.setShowNotification(!host.isShowNotification());
        ping.getHostService().persistHost(host);
        ping.getBus().post(host);
    }
}
