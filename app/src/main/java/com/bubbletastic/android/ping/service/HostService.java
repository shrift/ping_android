package com.bubbletastic.android.ping.service;

import com.bubbletastic.android.ping.model.Host;

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
}
