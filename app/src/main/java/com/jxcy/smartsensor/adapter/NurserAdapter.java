package com.jxcy.smartsensor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.view.unit.NurserKnow;

import java.util.List;

/**
 * 上拉加载更多
 */

public class NurserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NurserKnow> knows;
    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    public NurserAdapter(List<NurserKnow> dataList) {
        knows = dataList;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 通过判断显示类型，来创建不同的View
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nurser_item_layout, parent, false);
            return new NurserViewHolder(view);

        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foot_item_layout, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NurserViewHolder) {
            NurserViewHolder nurserHolder = (NurserViewHolder) holder;
            nurserHolder.initData(knows.get(position));
            nurserHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemListener != null) {
                        itemListener.OnItemListener(knows.get(position));
                    }
                }
            });
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    break;
                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    break;
                case LOADING_END: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return knows.size() + 1;
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void updateLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    public void updateLoadData(List<NurserKnow> data) {
        knows = data;
        notifyDataSetChanged();
    }

    private NuerserItemListener itemListener;

    public void setItemListener(NuerserItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public interface NuerserItemListener {
        void OnItemListener(NurserKnow item);
    }
}
