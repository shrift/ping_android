package com.bubbletastic.android.ping;

import android.app.job.JobParameters;
import android.app.job.JobService;

import java.util.Collections;
import java.util.List;

/**
 * Created by brendanmartens on 4/13/15.
 */
public class UpdateHostsService extends JobService {

    public static final int JOB_ID = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        updateHosts(params);
        return true;
    }

    private void updateHosts(final JobParameters params) {
        final Ping ping = (Ping) getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HostsUpdating hostsUpdating = new HostsUpdating(true);
                ping.getBus().post(hostsUpdating);

                List<Host> hosts = ping.getHostService().retrievePersistedHosts();

                //if these are not sorted they don't update in the order they appear in the rest of the app which can look odd.
                Collections.sort(hosts);

                for (Host host : hosts) {
                    ping.getHostService().updateHost(ping.getHostService().refreshHost(host));
                }

                hostsUpdating.setUpdating(false);
                ping.getBus().post(hostsUpdating);
                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
