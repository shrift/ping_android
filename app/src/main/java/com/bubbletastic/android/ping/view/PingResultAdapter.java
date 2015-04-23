package com.bubbletastic.android.ping.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bubbletastic.android.ping.R;
import com.bubbletastic.android.ping.model.proto.PingResult;

import java.util.List;

/**
 * Created by brendanmartens on 4/5/15.
 */
public class PingResultAdapter extends BaseAdapter {

    private List<PingResult> pingResults;
    private Context context;

    public PingResultAdapter(Context context, List<PingResult> pingResults) {
        this.pingResults = pingResults;
        this.context = context;
    }

    public int getPositionOfHost(PingResult pingResult) {
        return pingResults.indexOf(pingResult);
    }

    @Override
    public int getCount() {
        if (pingResults == null) {
            return 0;
        }
        return pingResults.size();
    }

    @Override
    public PingResult getItem(int position) {
        return pingResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        View indicator;
        TextView roundTripAvg;
        TextView pingedAt;
        PingResultViewHolder pingResultViewHolder;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            view = mInflater.inflate(R.layout.ping_result_list_item, parent, false);
            indicator = view.findViewById(R.id.ping_result_list_item_host_status_indicator);
            roundTripAvg = (TextView) view.findViewById(R.id.ping_result_list_item_round_trip_avg);
            pingedAt = (TextView) view.findViewById(R.id.ping_result_list_item_pinged_at);
            pingResultViewHolder = new PingResultViewHolder();
            pingResultViewHolder.indicator = indicator;
            pingResultViewHolder.roundTripAvg = roundTripAvg;
            pingResultViewHolder.pingedAt = pingedAt;
            view.setTag(pingResultViewHolder);
        } else {
            view = convertView;
            pingResultViewHolder = (PingResultViewHolder) view.getTag();
            indicator = pingResultViewHolder.indicator;
            roundTripAvg = pingResultViewHolder.roundTripAvg;
            pingedAt = pingResultViewHolder.pingedAt;
        }
        PingResult pingResult = (PingResult) getItem(position);

        indicator.setBackgroundResource(R.drawable.round_indicator_host_unknown);
        roundTripAvg.setText(pingResult.round_trip_avg + " ms");

        pingResultViewHolder.updatePingResultStatusInfo(pingResult);

        return view;
    }
}
