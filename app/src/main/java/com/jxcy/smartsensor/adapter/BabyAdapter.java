package com.jxcy.smartsensor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.greendao.BabyEntity;

import java.util.List;

public class BabyAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<BabyEntity> hlist;

    public BabyAdapter(Context context, List<BabyEntity> list) {
        mContext = context;
        hlist = list;
    }

    public void updateData(List<BabyEntity> upList) {
        if (upList != null && upList.size() > 0) {
            hlist = upList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new BabyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.baby_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        BabyViewHolder holder = (BabyViewHolder) viewHolder;
        holder.initData(hlist.get(position));
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    itemListener.OnItemListener(hlist.get(position));
                }
            }
        });
        holder.addDelListener(new BabyViewHolder.DelListener() {
            @Override
            public void delItem(BabyEntity curBaby) {
                hlist.remove(curBaby);
                if(itemListener!=null){
                    itemListener.delItem(curBaby);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return hlist != null ? hlist.size() : 0;
    }

    BabyItemListener itemListener;

    public void setItemListener(BabyItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface BabyItemListener {
        void OnItemListener(BabyEntity item);
        void delItem(BabyEntity entity);
    }
}
