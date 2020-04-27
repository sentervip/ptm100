package com.jxcy.smartsensor.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jxcy.smartsensor.R;

public class UnitViewHolder extends RecyclerView.ViewHolder {
    private RadioButton soundRadio;
    private TextView unit_v;

    public UnitViewHolder(@NonNull View itemView) {
        super(itemView);
        soundRadio = itemView.findViewById(R.id.sound_radio);
        unit_v = itemView.findViewById(R.id.sound_name);
    }

    public void initData(UnitEntity unit) {
        if (unit != null) {
            unit_v.setText(unit.getUnitValue());
            unit_v.setSelected(unit.isCheck());
            soundRadio.setChecked(unit.isCheck());
        }
    }
}
