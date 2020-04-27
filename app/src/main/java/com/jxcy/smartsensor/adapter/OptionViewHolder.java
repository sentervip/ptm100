package com.jxcy.smartsensor.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.greendao.BabyEntity;

public class OptionViewHolder extends RecyclerView.ViewHolder {
    private TextView babyName;
    private SimpleDraweeView draweeView;
    private RadioButton radioButton;

    public OptionViewHolder(@NonNull View itemView) {
        super(itemView);
        babyName = itemView.findViewById(R.id.baby_name);
        draweeView = itemView.findViewById(R.id.drawee_view);
        radioButton = itemView.findViewById(R.id.baby_radio_btn);
    }

    public void initData(BabyEntity entity) {
        if (entity != null) {
            babyName.setText(entity.getNickName());
            draweeView.setImageURI(Uri.parse("file://" + entity.getHead_url()));
            boolean isChecked = entity.getIsCheck()==1;
            radioButton.setChecked(isChecked);
            babyName.setSelected(isChecked);
        }
    }
}
