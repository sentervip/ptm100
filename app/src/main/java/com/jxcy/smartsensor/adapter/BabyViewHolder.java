package com.jxcy.smartsensor.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.greendao.BabyEntity;

public class BabyViewHolder extends RecyclerView.ViewHolder {
    private TextView babyName;
    private TextView babyAge;
    private SimpleDraweeView draweeView;
    private BabyEntity curBaby;
    View delete,item_layout;
    public BabyViewHolder(@NonNull View itemView) {
        super(itemView);
        babyName = itemView.findViewById(R.id.child_name);
        babyAge = itemView.findViewById(R.id.age_v);
        draweeView = itemView.findViewById(R.id.head_icon);
        item_layout = itemView.findViewById(R.id.view_item);
        delete = itemView.findViewById(R.id.del_layout);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delListener!=null){
                    delListener.delItem(curBaby);
                }
            }
        });
    }

    public void initData(BabyEntity entity) {
        if (entity != null) {
            curBaby = entity;
            babyName.setText(entity.getNickName());
            babyAge.setText(String.valueOf(entity.getBabyAge()));
            draweeView.setImageURI(Uri.parse("file://" + entity.getHead_url()));
        }
    }


    private DelListener delListener;

    public void addDelListener(DelListener listener) {
        delListener = listener;
    }

    public interface DelListener {
        void delItem(BabyEntity curBaby);
    }
}
