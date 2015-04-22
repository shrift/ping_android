package com.bubbletastic.android.ping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.bubbletastic.android.ping.model.proto.HostStatus;
import com.bubbletastic.android.ping.model.proto.ProtoHost;
import com.bubbletastic.android.ping.model.proto.HostsContainer;
import com.squareup.wire.Wire;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by brendanmartens on 4/20/15.
 */
public class HostService {

    private static final String PREF_KEY_HOSTS = "saved_hosts";
    private static final String PREFS_NAME = "app_preferences";

    private static HostService instance;
    private Context context;
    private SharedPreferences prefs;

    private HostService() {
    }

    public static HostService getInstance(Context context) {
        if (instance == null) {
            instance = new HostService();
            instance.context = context;
            instance.prefs = instance.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        return instance;
    }

    /**
     * This method will update the passed host. This operation performs calls on the network and should not be performed on the main thread.
     *
     * @param host
     * @return
     */
    public Host refreshHost(Host host) {
        Ping ping = (Ping) context;

        HostStatus status = HostStatus.updating;
        host.setStatus(status);
        ping.getBus().post(host);

        InetAddress address = null;
        try {
            address = InetAddress.getByName(host.getHostName());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host " + host.getHostName());
            status = HostStatus.unreachable;
        }

        if (address != null) {
            //check the host 4 times
            for (int i = 0; i < 4; i++) {
                if (status == HostStatus.unreachable) {
                    //require that all attempts are successful
                    //(the initial ping attempt will always be "updating" due to the first line of the method, but subsequent iterations may be "unreachable")
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
        }
        host.setRefreshed(new Date());
        host.setStatus(status);
        ping.getBus().post(host);

        return host;
    }

    /**
     * This method updates a persisted host, but will not add the host if it does not already exist.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     *
     * @param host
     */
    public synchronized void updateHost(Host host) {
        List<Host> hosts = retrievePersistedHosts();

        //do nothing if the list didn't contain the host.
        if (!hosts.contains(host)) {
            return;
        }

        hosts.remove(host);
        hosts.add(host);
        saveHostsOverwriting(hosts);
    }

    /**
     * This method adds a list of hosts to persistent storage.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     * This method will overwrite what currently exists in storage.
     *
     * @param hosts
     */
    public synchronized void persistHostsOverwrite(List<Host> hosts) {
        saveHostsOverwriting(hosts);
    }

    /**
     * This method adds a host to persistent storage. If the hosts already exists it will be overwritten.
     * This method may block if another write operation is in progress and therefore should not be called on the main thread.
     *
     * @param host
     */
    public synchronized void persistHost(Host host) {
        List<Host> hosts = retrievePersistedHosts();
        if (hosts == null) {
            hosts = new ArrayList<>();
        }

        if (hosts.contains(host)) {
            hosts.remove(host);
        }
        hosts.add(host);

        saveHostsOverwriting(hosts);
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
        List<Host> hosts = new ArrayList<>();
        String hostsJson = prefs.getString(PREF_KEY_HOSTS, null);
        if (hostsJson != null) {
            byte[] bytes = Base64.decode(hostsJson, Base64.NO_WRAP);
            Wire wire = new Wire();
            try {
                HostsContainer hostsContainer = wire.parseFrom(bytes, HostsContainer.class);
                for (ProtoHost protoHost : hostsContainer.hosts) {
                    hosts.add(new Host(protoHost));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hosts;
    }

    /**
     * Protocol Buffers are used to store data so that we have a schema that can evolve, avoiding nasty upgrade issues if the Host objects change.
     * @param hosts
     */
    @SuppressLint("CommitPrefEdits")
    private synchronized void saveHostsOverwriting(List<Host> hosts) {
        List<ProtoHost> protoHosts = new ArrayList<ProtoHost>();
        for (Host host : hosts) {
            protoHosts.add(host.toProtoHost());
        }

        byte[] bytes = new HostsContainer.Builder().hosts(protoHosts).build().toByteArray();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_KEY_HOSTS, Base64.encodeToString(bytes, Base64.NO_WRAP));
        editor.commit();
    }
}
