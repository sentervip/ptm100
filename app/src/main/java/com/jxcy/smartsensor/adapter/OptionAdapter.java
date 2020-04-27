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

public class OptionAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<BabyEntity> babyList;

    public OptionAdapter(Context context, List<BabyEntity> list) {
        mContext = context;
        babyList = list;
    }

    public void updateData(List<BabyEntity> upList) {
        if (upList != null && upList.size() > 0) {
            babyList = upList;
            notifyDataSetChanged();
        }
    }

    private void updateCheckNo(long id) {
        for (BabyEntity baby : babyList) {
            if (baby != null && baby.getId() == id) {
                baby.setIsCheck(1);
            } else {
                baby.setIsCheck(0);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new OptionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.option_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        OptionViewHolder holder = (OptionViewHolder) viewHolder;
        holder.initData(babyList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    itemListener.OnItemListener(babyList.get(position));
                    updateCheckNo(babyList.get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return babyList != null ? babyList.size() : 0;
    }

    OptionItemListener itemListener;

    public void setItemListener(OptionItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface OptionItemListener {
        void OnItemListener(BabyEntity item);
    }
}
