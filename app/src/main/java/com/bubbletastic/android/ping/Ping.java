package com.bubbletastic.android.ping;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bubbletastic.android.ping.service.HostService;
import com.bubbletastic.android.ping.service.HostServiceImpl;
import com.bubbletastic.android.ping.service.RefreshHostsJobService;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by brendanmartens on 4/13/15.
 */
public class Ping extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Bus bus;
    private JobScheduler jobScheduler;
    private HostService hostService;
    private SharedPreferences sharedPref;


    @Override
    public void onCreate() {
        super.onCreate();

        bus = new Bus(ThreadEnforcer.ANY);
        hostService = HostServiceImpl.getInstance(this);

        //schedule the update hosts job
        jobScheduler = ((JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE));
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        scheduleHostRefreshJob();
    }

    private void scheduleHostRefreshJob() {
        long intervalMilliSeconds = sharedPref.getLong(
                getString(R.string.pref_key_global_refresh_interval),
                getResources().getInteger(R.integer.default_refresh_interval))
                * 1000;

        int networkType;
        boolean wifiOnly = sharedPref.getBoolean(getString(R.string.pref_key_wifi_only), false);
        JobInfo updateHostsJob = new JobInfo.Builder(RefreshHostsJobService.JOB_ID, new ComponentName(this, RefreshHostsJobService.class))
                .setRequiredNetworkType(wifiOnly ? JobInfo.NETWORK_TYPE_UNMETERED : JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(intervalMilliSeconds)
                        //service scheduling should survive reboots
                .setPersisted(true)
                .build();
        jobScheduler.schedule(updateHostsJob);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        jobScheduler.cancel(RefreshHostsJobService.JOB_ID);
        scheduleHostRefreshJob();
    }

    public Bus getBus() {
        return bus;
    }

    public HostService getHostService() {
        return hostService;
    }

}
