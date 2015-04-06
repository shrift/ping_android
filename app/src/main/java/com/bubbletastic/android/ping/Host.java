package com.bubbletastic.android.ping;

import java.net.InetAddress;

public class Host {
    public String hostName;
    public InetAddress address;

    public Host() { }

    public Host(String hostName) {
//        address = InetAddress.getByName(hostName);
        this.hostName = hostName;
    }

    @Override
    public String toString() {
        return hostName;
    }
}
