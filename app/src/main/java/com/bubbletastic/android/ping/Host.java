package com.bubbletastic.android.ping;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class Host implements Comparable {

    private String hostName;
    private Date refreshed;
    private HostStatus status;

    public Host() {
    }

    public Host(String hostName) {
        this.hostName = hostName;
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
        return refreshed;
    }

    public void setRefreshed(Date refreshed) {
        this.refreshed = refreshed;
    }

    public HostStatus getStatus() {
        return status;
    }

    public void setStatus(HostStatus status) {
        this.status = status;
    }
}
