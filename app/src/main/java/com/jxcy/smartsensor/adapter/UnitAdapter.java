package com.jxcy.smartsensor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.jxcy.smartsensor.R;

import java.util.List;
import java.util.Map;

public class UnitAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<UnitEntity> unitArray;

    public UnitAdapter(Context context, List<UnitEntity> data) {
        this.mContext = context;
        unitArray = data;
    }

    private void initData(int position) {
        for (UnitEntity unit : unitArray) {
            unit.setCheck(false);
        }
        unitArray.get(position).setCheck(true);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        return new UnitViewHolder(LayoutInflater.from(mContext).inflate(R.layout.unit_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        UnitViewHolder unitHolder = (UnitViewHolder) viewHolder;
        unitHolder.initData(unitArray.get(position));
        unitHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(position);
                if (clickListener != null) {
                    clickListener.onItemClick(unitArray.get(position).getUnitValue());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return unitArray != null ? unitArray.size() : 0;
    }

    private ItemClickListener clickListener;

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(String value);
    }
}

