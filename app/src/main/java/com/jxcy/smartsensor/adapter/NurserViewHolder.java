package com.jxcy.smartsensor.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.view.unit.NurserKnow;

public class NurserViewHolder extends RecyclerView.ViewHolder {
    private TextView know_title;
    private TextView description;
    private SimpleDraweeView simpleDraweeView;

    public NurserViewHolder(@NonNull View itemView) {
        super(itemView);
        know_title = itemView.findViewById(R.id.know_title);
        description = itemView.findViewById(R.id.description);
        simpleDraweeView = itemView.findViewById(R.id.url_image);
    }

    public void initData(NurserKnow entity) {
        if (entity != null) {
            know_title.setText(entity.getTitle());
            description.setText(entity.getDescription());
            Uri uri = Uri.parse(entity.getUrl());
            simpleDraweeView.setImageURI(uri);
        }
    }
}
