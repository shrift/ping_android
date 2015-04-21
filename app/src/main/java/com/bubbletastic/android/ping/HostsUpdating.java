package com.bubbletastic.android.ping;

/**
 * Created by brendanmartens on 4/20/15.
 */
public class HostsUpdating {
    private boolean updating;

    public HostsUpdating(boolean updating) {
        this.updating = updating;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }
}
