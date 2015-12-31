package com.bubbletastic.android.ping.userinterface.view;

import android.view.View;
import android.widget.TextView;

import com.bubbletastic.android.ping.model.Host;
import com.bubbletastic.android.ping.R;

/**
 * Created by brendanmartens on 4/22/15.
 */
public class HostViewHolder {
    public TextView hostName;
    public TextView updated;
    public View indicator;

    public void updateHostStatusInfo(Host host) {

        switch (host.getCurrentStatus()) {
            case unreachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unreachable);
                updated.setText(null);
                break;
            case reachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_reachable);
                updated.setText(null);
                break;
            case disconnected:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_disconnected);
                updated.setText(updated.getContext().getString(R.string.host_disconnected));
                break;
            case updating:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
                updated.setText(updated.getContext().getString(R.string.host_updating));
                break;
            default:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
                updated.setText(updated.getContext().getString(R.string.updated_unknown));
                break;

        }
    }
}
