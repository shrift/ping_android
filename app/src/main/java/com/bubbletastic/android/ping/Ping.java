package com.bubbletastic.android.ping;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by brendanmartens on 4/13/15.
 */
public class Ping extends Application {

    private static final String PREF_KEY_HOSTS = "saved_hosts";
    private static final String PREFS_NAME = "app_preferences";
    private Lock persistenceLock = new ReentrantLock();

    private SharedPreferences prefs;


    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);


        JobInfo updateHostsJob = new JobInfo.Builder(UpdateHostsService.JOB_ID, new ComponentName(this, UpdateHostsService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        //update hosts once a minute
                .setPeriodic(60000)
                .build();

        //schedule the update hosts job
        ((JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE)).schedule(updateHostsJob);
    }

    /**
     * This method updates a persisted host, but will not add the host if it does not already exist.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     *
     * @param host
     */
    public synchronized void updateHost(Host host) {
//        synchronized (persistenceLock) {
        List<Host> hosts = retrievePersistedHosts();

        //do nothing if the list didn't contain the host.
        if (!hosts.contains(host)) {
            return;
        }

        hosts.remove(host);
        hosts.add(host);
        saveHostsOverwriting(hosts);
//        }
    }

    /**
     * This method adds a list of hosts to persistent storage.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     * This method will overwrite what currently exists in storage.
     *
     * @param hosts
     */
    public synchronized void persistHostsOverwrite(List<Host> hosts) {
//        synchronized (persistenceLock) {
        saveHostsOverwriting(hosts);
//        }
    }

    /**
     * This method adds a host to persistent storage. If the hosts already exists it will be overwritten.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     *
     * @param host
     */
    public synchronized void persistHost(Host host) {
//        synchronized (persistenceLock) {

        List<Host> hosts = retrievePersistedHosts();
        if (hosts == null) {
            hosts = new ArrayList<>();
        }

        if (hosts.contains(host)) {
            hosts.remove(host);
        }
        hosts.add(host);

        saveHostsOverwriting(hosts);
//        }
    }

    /**
     * This method removes hosts from persistent storage.
     * This method may block if a write operation is in progress and therefore should not be called on the main thread.
     *
     * @param hostsForRemoval
     */
    public synchronized void removeHosts(List<Host> hostsForRemoval) {
        List<Host> hosts = retrievePersistedHosts();
        hosts.removeAll(hostsForRemoval);
        saveHostsOverwriting(hosts);
    }

    /**
     * This method remove a host from persistent storage.
     * This method may block if a write operation is in progress and therefore should not be called on the main thread.
     *
     * @param hostToRemove
     */
    public synchronized void removeHost(Host hostToRemove) {
        List<Host> hosts = retrievePersistedHosts();
        hosts.remove(hostToRemove);
        saveHostsOverwriting(hosts);
    }

    /**
     * This method retrieves hosts from persistent storage.
     * This method may block if a write operation is in progress and therefore should not be called on the main thread.
     *
     * @return
     */
    public synchronized List<Host> retrievePersistedHosts() {
//        synchronized (persistenceLock) {
        List<Host> hosts = new ArrayList<>();
        String hostsJson = prefs.getString(PREF_KEY_HOSTS, null);
        if (hostsJson != null) {
            Gson gson = new Gson();
            Type hostsType = new TypeToken<ArrayList<Host>>() {
            }.getType();
            hosts = gson.fromJson(hostsJson, hostsType);
        }
        return hosts;
//        }
    }

    private void saveHostsOverwriting(List<Host> hosts) {
        Gson gson = new Gson();
        Type hostsType = new TypeToken<ArrayList<Host>>() {
        }.getType();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_KEY_HOSTS, gson.toJson(hosts, hostsType));
        editor.commit();
    }

}
