package com.bubbletastic.android.ping.service;

import android.annotation.SuppressLint;

import com.bubbletastic.android.ping.model.Host;
import com.bubbletastic.android.ping.model.proto.PingResult;

import java.util.List;

/**
 * Created by brendanmartens on 12/31/15.
 */
public interface HostService {
    /**
     * This method will check that the passed host is reachable as many times as configured to count as "refreshed".
     * This operation performs calls on the network and should not be performed on the main thread.
     *
     * @param host The host to refresh.
     * @return The refreshed Host.
     */
    Host refreshHost(Host host);

    Integer getMean(int[] times);

    /**
     * Shows notifications for hosts depending on preferences.
     *
     * @param host The host to evaluate for notification.
     */
    void postNotification(Host host);

    /**
     * This method will ping the passed host.
     * This operation performs calls on the network and should not be performed on the main thread.
     *
     * @param host    The Host to ping.
     * @param timeout How long to wait for a ping response before giving up.
     * @return The PingResult from pinging the Host.
     */
    PingResult pingHost(Host host, int timeout);

    boolean isNetworkAvailable();

    /**
     * This method updates a persisted host, but will not add the host if it does not already exist.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     *
     * @param host
     */
    void updateHost(Host host);

    /**
     * This method adds a list of hosts to persistent storage.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     * This method will overwrite what currently exists in storage.
     *
     * @param hosts
     */
    void persistHostsOverwrite(List<Host> hosts);

    /**
     * This method adds a host to persistent storage. If the hosts already exists it will be overwritten.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     *
     * @param host
     */
    void persistHost(Host host);

    /**
     * This method removes hosts from persistent storage.
     * This method may block if a write operation is in progress and therefore should not be called on the main thread.
     *
     * @param hostsForRemoval
     */
    void removeHosts(List<Host> hostsForRemoval);

    /**
     * This method remove a host from persistent storage.
     * This method may block if a write operation is in progress and therefore should not be called on the main thread.
     *
     * @param hostToRemove
     */
    void removeHost(Host hostToRemove);

    /**
     * This method retrieves a host from persistent storage.
     * This method may block if a write operation is in progress and therefore should not be called on the main thread.
     *
     * @return
     */
    Host retrievePersistedHost(String hostName);

    /**
     * This method retrieves hosts from persistent storage.
     * This method may block if a write operation is in progress and therefore should not be called on the main thread.
     *
     * @return
     */
    List<Host> retrievePersistedHosts();

    /**
     * Protocol Buffers are used to store data so that we have a schema that can evolve, avoiding nasty upgrade issues if the Host objects change.
     *
     * @param hosts
     */
    @SuppressLint("CommitPrefEdits")
    void saveHostsOverwriting(List<Host> hosts);
}
