package com.bubbletastic.android.ping.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.bubbletastic.android.ping.Ping;
import com.bubbletastic.android.ping.R;
import com.bubbletastic.android.ping.model.Host;
import com.bubbletastic.android.ping.model.proto.HostStatus;
import com.bubbletastic.android.ping.model.proto.HostsContainer;
import com.bubbletastic.android.ping.model.proto.PingResult;
import com.bubbletastic.android.ping.model.proto.ProtoHost;
import com.bubbletastic.android.ping.userinterface.HostDetailActivity;
import com.bubbletastic.android.ping.userinterface.HostDetailFragment;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brendanmartens on 4/20/15.
 */
public class HostServiceImpl implements HostService {

    private static final String PREF_KEY_HOSTS = "saved_hosts";
    private static final String PREFS_NAME = "app_preferences";

    private static HostServiceImpl instance;

    private Context context;
    private Ping ping;
    private SharedPreferences appPrefs;
    private SharedPreferences defaultSharedPrefs;

    private HostServiceImpl() {
    }

    public static HostService getInstance(Context context) {
        if (instance == null) {
            instance = new HostServiceImpl();
            instance.context = context;
            instance.ping = (Ping) context.getApplicationContext();
            instance.appPrefs = instance.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            instance.defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return instance;
    }

    @Override
    public Host refreshHost(Host host) {

        HostStatus status = HostStatus.updating;
        host.setCurrentStatus(status);
        //Post the host so UI can get the updating status.
        ping.getBus().post(host);

        //Setup time for use later when we return the final results.
        Integer time = null;

        try {
            InetAddress address = null;
            if (isNetworkAvailable()) {
                try {
                    address = InetAddress.getByName(host.getHostName());
                } catch (UnknownHostException e) {
                    System.err.println("Unknown host " + host.getHostName());
                    status = HostStatus.unreachable;
                }
            } else {
                status = HostStatus.disconnected;
            }

            int attempts = (int) defaultSharedPrefs.getLong(
                    context.getString(R.string.pref_key_ping_refresh_count),
                    context.getResources().getInteger(R.integer.default_ping_refresh_count));

            int[] times = new int[attempts];
            if (address != null) {
                for (int i = 0; i < attempts; i++) {
                    PingResult pingResult = pingHost(host, 1000);
                    status = pingResult.status;

                    if (!status.equals(HostStatus.reachable)) {
                        break;
                    }

                    times[i] = pingResult.round_trip_avg != null ? pingResult.round_trip_avg : 0;
                }

                //Calculate the average round trip time for all attempts.
                time = getMean(times);
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = HostStatus.unknown;
        }

        host.setCurrentStatus(status);
        host.getResults().add(new PingResult.Builder()
                .pinged_at(System.currentTimeMillis())
                .round_trip_avg(time)
                .status(status)
                .build());
        ping.getBus().post(host);

        updateHost(host);

        postNotification(host);

        return host;
    }

    @Override
    public Integer getMean(int[] times) {
        if (times != null && times.length > 0) {
            int sum = 0;
            for (int attempt : times) {
                sum += attempt;
            }
            if (sum > 0) {
                return (int) ((double) sum / (double) times.length);
            }
        }
        return null;
    }

    @Override
    public void postNotification(Host host) {
        if (defaultSharedPrefs.getBoolean(context.getString(R.string.pref_key_show_unreachable_notifications), true) &&
                host.isShowNotification() && host.getCurrentStatus().equals(HostStatus.unreachable)) {

            Intent intent = new Intent(context, HostDetailActivity.class);
            intent.putExtra(HostDetailFragment.HOST_KEY, host.getHostName());

            Notification notification = new Notification.Builder(context)
                    .setContentTitle(context.getString(R.string.host_unreachable_notification_title).replace("$hostName$", host.getHostName()))
                    .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0))
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(
                    host.getHostName().hashCode() + host.getCurrentStatus().getValue() + host.getResults().size(),
                    notification);
        }
    }

    @Override
    public PingResult pingHost(Host host, int timeout) {
        if (host == null) {
            return null;
        }
        HostStatus status = HostStatus.unknown;
        InetAddress address = null;
        Integer time = null;

        try {
            address = InetAddress.getByName(host.getHostName());
        } catch (UnknownHostException e) {
            status = HostStatus.unreachable;
        }

        if (address != null) {
            try {
                long startTime = System.currentTimeMillis();
                boolean reachable = address.isReachable(timeout);
                long endTime = System.currentTimeMillis();
                if (reachable) {
                    status = HostStatus.reachable;
                } else {
                    status = HostStatus.unreachable;
                }
                time = (int) (endTime - startTime);
            } catch (IOException e) {
                status = HostStatus.unreachable;
            }
        }

        return new PingResult.Builder().pinged_at(System.currentTimeMillis()).status(status).round_trip_avg(time).build();
    }

    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
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

    @Override
    public synchronized void persistHostsOverwrite(List<Host> hosts) {
        saveHostsOverwriting(hosts);
    }

    @Override
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

    @Override
    public synchronized void removeHosts(List<Host> hostsForRemoval) {
        List<Host> hosts = retrievePersistedHosts();
        hosts.removeAll(hostsForRemoval);
        saveHostsOverwriting(hosts);
    }

    @Override
    public synchronized void removeHost(Host hostToRemove) {
        List<Host> hosts = retrievePersistedHosts();
        hosts.remove(hostToRemove);
        saveHostsOverwriting(hosts);
    }

    @Override
    public synchronized Host retrievePersistedHost(String hostName) {
        List<Host> hosts = retrievePersistedHosts();
        for (Host host : hosts) {
            if (host.getHostName().equals(hostName)) {
                return host;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Host> retrievePersistedHosts() {
        List<Host> hosts = new ArrayList<>();
        String hostsJson = appPrefs.getString(PREF_KEY_HOSTS, null);
        if (hostsJson != null) {
            byte[] bytes = Base64.decode(hostsJson, Base64.NO_WRAP);
            try {
                HostsContainer hostsContainer = HostsContainer.ADAPTER.decode(bytes);
                for (ProtoHost protoHost : hostsContainer.hosts) {
                    hosts.add(new Host(protoHost));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hosts;
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public synchronized void saveHostsOverwriting(List<Host> hosts) {
        List<ProtoHost> protoHosts = new ArrayList<ProtoHost>();
        for (Host host : hosts) {
            protoHosts.add(host.toProtoHost());
        }

        HostsContainer hostsContainer = new HostsContainer.Builder().hosts(protoHosts).build();
        byte[] bytes = HostsContainer.ADAPTER.encode(hostsContainer);
        SharedPreferences.Editor editor = appPrefs.edit();
        editor.putString(PREF_KEY_HOSTS, Base64.encodeToString(bytes, Base64.NO_WRAP));
        editor.commit();
    }
}
