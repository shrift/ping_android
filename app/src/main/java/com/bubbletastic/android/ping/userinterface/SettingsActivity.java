package com.bubbletastic.android.ping.userinterface;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bubbletastic.android.ping.R;

/**
 * Created by brendanmartens on 12/19/15.
 * <p/>
 * Note that this does not extend the PreferenceActivity as that activity is no longer recommended.
 * Instead, to easily achieve the action bar behavior desired, just wrap the PreferenceFragment in this standard activity.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
