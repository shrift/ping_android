package com.bubbletastic.android.ping;

import android.app.job.JobParameters;
import android.app.job.JobService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
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

//        for (Host host : hosts) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(new Date());
//            calendar.add(Calendar.MINUTE, -1);
//            Date oneMinuteAgo = calendar.getTime();
//
//            //initiate host refreshing and return true (tells the scheduler that work needs to be done in the background)
//            if (host.getRefreshed() == null || host.getRefreshed().before(oneMinuteAgo)) {
//                updateHosts(params, hosts);
//                return true;
//            }
//        }
    }

    private void updateHosts(final JobParameters params) {
        final Ping ping = (Ping) getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Host> hosts = ping.retrievePersistedHosts();

                for (Host host : hosts) {
//                for (int j = 0; j < hosts.size(); j++) {
//                    Host host = hosts.get(j);

                    HostStatus status = HostStatus.unknown;

                    InetAddress address = null;
                    try {
                        address = InetAddress.getByName(host.getHostName());
                    } catch (UnknownHostException e) {
                        //skip to the next host if this one could not be resolved
                        System.err.println("Unknown host " + host.getHostName());
                        continue;
                    }

                    //check the host 4 times
                    for (int i = 0; i < 4; i++) {
                        if (status == HostStatus.unreachable) {
                            //require that all attempts are successful
                            break;
                        }
                        try {
                            if (address.isReachable(3000)) {
                                status = HostStatus.reachable;
                            } else {
                                status = HostStatus.unreachable;
                            }
                        } catch (IOException e) {
                            System.err.println("Unable to reach " + host.getHostName());
                            status = HostStatus.unreachable;
                            break;
                        }
                    }

                    System.out.println(host.getHostName() + " is " + status);

                    host.setRefreshed(new Date());
                    host.setStatus(status);
                    ping.updateHost(host);
                }

                jobFinished(params, false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
