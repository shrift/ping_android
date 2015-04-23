package com.bubbletastic.android.ping.view;

import android.view.View;
import android.widget.TextView;

import com.bubbletastic.android.ping.Host;
import com.bubbletastic.android.ping.R;

/**
 * Created by brendanmartens on 4/22/15.
 */
public class HostViewHolder {
    public TextView hostName;
    public TextView updated;
    public View indicator;

    public void updateHostStatusInfo(Host host) {
//        String timeAgo = TimeUtil.getTimeAgo(host.getRefreshed().getTime());
//        if (timeAgo != null && !timeAgo.trim().isEmpty()) {
//            updateView.setText(timeAgo);
//        }

        switch (host.getCurrentStatus()) {
            case unreachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unreachable);
                updated.setText(null);
                break;
            case reachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_reachable);
                updated.setText(null);
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
