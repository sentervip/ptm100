package com.jxcy.smartsensor.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jxcy.smartsensor.R;

public class HospitalViewHolder extends RecyclerView.ViewHolder {
    private TextView hospitalName;
    private TextView hospitalAddress;

    public HospitalViewHolder(@NonNull View itemView) {
        super(itemView);
        hospitalName = itemView.findViewById(R.id.hospital_name);
        hospitalAddress = itemView.findViewById(R.id.hospital_address);
    }

    public void initData(HospitalEntity entity) {
        if (entity != null) {
            hospitalName.setText(entity.getName());
            hospitalAddress.setText(entity.getAddress());
        }
    }
}
