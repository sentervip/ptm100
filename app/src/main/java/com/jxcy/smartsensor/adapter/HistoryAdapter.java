package com.jxcy.smartsensor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.greendao.DayRecord;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter {
    private List<DayRecord> records;
    private Context mContext;

    public HistoryAdapter(Context context, List<DayRecord> mlist) {
        mContext = context;
        records = mlist;
    }

    public void updateHistory(List<DayRecord> upList){
        if(upList!=null){
            records = upList;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new HistoryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.history_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        HistoryViewHolder historyViewHolder = (HistoryViewHolder) viewHolder;
        historyViewHolder.initData(records.get(position));
        historyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(historyViewHolder.getDetailView()==null) {
                    ViewStub viewStub = historyViewHolder.itemView.findViewById(R.id.detail_stub);
                    View view = viewStub.inflate();
                    historyViewHolder.setDetailView(view);
                    historyViewHolder.loadHistoryRecord();
                }else {
                    if(historyViewHolder.getDetailView().getVisibility()==View.GONE){
                        historyViewHolder.getDetailView().setVisibility(View.VISIBLE);
                        historyViewHolder.loadHistoryRecord();
                    }else {
                        historyViewHolder.getDetailView().setVisibility(View.GONE);
                        historyViewHolder.cancelTask();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return records != null ? records.size() : 0;
    }
}
