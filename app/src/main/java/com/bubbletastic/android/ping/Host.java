package com.bubbletastic.android.ping;

import com.bubbletastic.android.ping.model.proto.HostStatus;
import com.bubbletastic.android.ping.model.proto.PingResult;
import com.bubbletastic.android.ping.model.proto.ProtoHost;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Host implements Comparable {

    private String hostName;
    private List<PingResult> results;
    private HostStatus currentStatus = HostStatus.unknown;

    public Host() {
    }

    public Host(String hostName) {
        this.hostName = hostName;
    }

    public Host(ProtoHost protoHost) {
        this.hostName = protoHost.host_name;
    }

    public ProtoHost toProtoHost() {
        ProtoHost.Builder protoHostBuilder = new ProtoHost.Builder();
        return protoHostBuilder.host_name(hostName).results(results).build();
    }

    @Override
    public String toString() {
        return hostName;
    }

    @Override
    public int hashCode() {
        if (hostName == null) {
            return 0;
        }
        return hostName.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o instanceof Host && ((Host) o).getHostName().equals(this.getHostName());
    }

    @Override
    public int compareTo(Object another) {
        return this.toString().compareTo(another.toString());
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByName(hostName);
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Date getRefreshed() {
        if (results != null) {
            sortResultsByDate();
            return new Date(results.get(0).pinged_at);
        }
        return null;
    }

    public List<PingResult> getResults() {
        if (results == null) {
            return new ArrayList<PingResult>();
        }
        return results;
    }

    public void setResults(List<PingResult> results) {
        this.results = results;
    }

    private void sortResultsByDate() {
        if (results != null) {
            Collections.sort(results, new Comparator<PingResult>() {
                @Override
                public int compare(PingResult lhs, PingResult rhs) {
                    return lhs.pinged_at.compareTo(rhs.pinged_at);
                }
            });
        }
    }

    public HostStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(HostStatus currentStatus) {
        this.currentStatus = currentStatus;
    }
}
