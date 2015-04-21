package com.bubbletastic.android.ping;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by brendanmartens on 4/13/15.
 */
public class Ping extends Application {

    private Bus bus;
    private JobScheduler jobScheduler;
    private HostService hostService;


    @Override
    public void onCreate() {
        super.onCreate();

        bus = new Bus(ThreadEnforcer.ANY);
        hostService = HostService.getInstance(this);

        JobInfo updateHostsJob = new JobInfo.Builder(UpdateHostsService.JOB_ID, new ComponentName(this, UpdateHostsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        //update hosts once a minute
                .setPeriodic(60000)
                        //service scheduling should survive reboots
                .setPersisted(true)
                .build();

        //schedule the update hosts job
        jobScheduler = ((JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE));
        jobScheduler.schedule(updateHostsJob);

        //kick off an initial hosts update at app launch
        refreshHostsSoon();
    }

    /**
     * This method will schedule an update to the hosts, but due to the way the system scheduler service works it may not be executed immediately (thus "soon").
     */
    public void refreshHostsSoon() {
        JobInfo updateHostsJob = new JobInfo.Builder(UpdateHostsService.JOB_ID, new ComponentName(this, UpdateHostsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setOverrideDeadline(250)
                .build();

        //schedule the update hosts job
        jobScheduler.schedule(updateHostsJob);
    }

    public Bus getBus() {
        return bus;
    }

    public HostService getHostService() {
        return hostService;
    }
}
