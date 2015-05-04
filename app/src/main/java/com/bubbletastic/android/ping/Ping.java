package com.bubbletastic.android.ping;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;

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
                        //update hosts once every 4 minutes
                .setPeriodic(240000)
                        //service scheduling should survive reboots
                .setPersisted(true)
                .build();

        //schedule the update hosts job
//        jobScheduler = ((JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE));
//        jobScheduler.schedule(updateHostsJob);
    }

    public Bus getBus() {
        return bus;
    }

    public HostService getHostService() {
        return hostService;
    }
}
