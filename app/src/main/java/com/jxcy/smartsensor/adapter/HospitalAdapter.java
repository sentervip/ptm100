package com.jxcy.smartsensor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.search.core.PoiInfo;
import com.jxcy.smartsensor.R;

import java.util.List;
import java.util.zip.Inflater;

public class HospitalAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<HospitalEntity> hlist;

    public HospitalAdapter(Context context, List<HospitalEntity> list) {
        mContext = context;
        hlist = list;
    }

    public void updateData(List<HospitalEntity> upList) {
        if (upList != null && upList.size() > 0) {
            hlist = upList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new HospitalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.hospital_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        HospitalViewHolder holder = (HospitalViewHolder) viewHolder;
        holder.initData(hlist.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemListener != null) {
                    itemListener.OnItemListener(hlist.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return hlist != null ? hlist.size() : 0;
    }

    HospitalItemListener itemListener;

    public void setItemListener(HospitalItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface HospitalItemListener {
        void OnItemListener(HospitalEntity item);
    }
}
