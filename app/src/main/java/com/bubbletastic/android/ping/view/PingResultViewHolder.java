package com.bubbletastic.android.ping.view;

import android.view.View;
import android.widget.TextView;

import com.bubbletastic.android.ping.R;
import com.bubbletastic.android.ping.model.proto.PingResult;
import com.google.TimeUtil;

/**
 * Created by brendanmartens on 4/22/15.
 */
public class PingResultViewHolder {
    public TextView roundTripAvg;
    public TextView pingedAt;
    public View indicator;

    public void updatePingResultStatusInfo(PingResult pingResult) {
        String timeAgo = TimeUtil.getTimeAgo(pingResult.pinged_at);
        if (timeAgo != null && !timeAgo.trim().isEmpty()) {
            pingedAt.setText(timeAgo);
        }

        switch (pingResult.status) {
            case unreachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unreachable);
                break;
            case reachable:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_reachable);
                break;
            case updating:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
                break;
            default:
                indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
                break;

        }
    }
}
