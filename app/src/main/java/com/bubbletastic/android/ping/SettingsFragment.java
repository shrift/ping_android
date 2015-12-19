package com.bubbletastic.android.ping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by brendanmartens on 12/18/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference refreshIntervalPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        refreshIntervalPref = (EditTextPreference) findPreference(getString(R.string.pref_key_global_refresh_interval));
    }

    @Override
    public void onResume() {
        setRefreshIntervalSummary();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        view.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), android.R.color.white));
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_global_refresh_interval))) {
            setRefreshIntervalSummary();
        }
    }

    private void setRefreshIntervalSummary() {
        refreshIntervalPref.setSummary(getString(R.string.ping_interval_summary).replace("$seconds$", refreshIntervalPref.getText()));
    }
}
