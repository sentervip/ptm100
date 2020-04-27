package com.jxcy.smartsensor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jxcy.smartsensor.R;

public class FootViewHolder extends RecyclerView.ViewHolder {

    ProgressBar pbLoading;
    TextView tvLoading;

    FootViewHolder(View itemView) {
        super(itemView);
        pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
        tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
    }
}
