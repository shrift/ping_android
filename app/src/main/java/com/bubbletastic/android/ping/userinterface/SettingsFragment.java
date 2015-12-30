package com.bubbletastic.android.ping.userinterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.bubbletastic.android.ping.R;
import com.bubbletastic.android.ping.userinterface.view.LongEditTextPreference;

/**
 * Created by brendanmartens on 12/18/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private LongEditTextPreference refreshIntervalPref;
    private LongEditTextPreference refreshPingCountPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
        refreshIntervalPref = (LongEditTextPreference) findPreference(getString(R.string.pref_key_global_refresh_interval));
        refreshPingCountPref = (LongEditTextPreference) findPreference(getString(R.string.pref_key_ping_refresh_count));
    }

    @Override
    public void onResume() {
        setRefreshIntervalSummary();
        setRefreshPingCountSummary();

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
        if (key.equals(getString(R.string.pref_key_global_refresh_interval))) {
            setRefreshIntervalSummary();
        } else if (key.equals(getString(R.string.pref_key_ping_refresh_count))) {
            setRefreshPingCountSummary();
        }
    }

    private void setRefreshIntervalSummary() {
        refreshIntervalPref.setSummary(getString(R.string.ping_interval_summary).replace("$seconds$", refreshIntervalPref.getText()));
    }

    private void setRefreshPingCountSummary() {
        refreshPingCountPref.setSummary(getString(R.string.ping_refresh_count_summary).replace("$count$", refreshPingCountPref.getText()));
    }
}
