package com.jxcy.smartsensor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hndw.smartlibrary.until.DateTools;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.greendao.DayRecord;
import com.jxcy.smartsensor.presenter.HistoryPresenter;
import com.jxcy.smartsensor.presenter.impl.HistoryPresenterImpl;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.view.ui.HistoryUi;

public class HistoryViewHolder extends RecyclerView.ViewHolder implements HistoryUi {
    private TextView max_v;
    private TextView start_v, end_v;
    private ImageView temp_icon;
    private Context context;
    private View detailView;
    private HistoryPresenter presenter;
    private String day_id;

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        max_v = itemView.findViewById(R.id.max_v);
        start_v = itemView.findViewById(R.id.start_v);
        end_v = itemView.findViewById(R.id.end_v);
        temp_icon = itemView.findViewById(R.id.tem_icon);
        context = itemView.getContext();
        presenter = new HistoryPresenterImpl();
    }

    public void initData(DayRecord dayRecord) {
        if (dayRecord != null) {
            float maxValue = dayRecord.getMaxTemperature();
            if (maxValue > Contants.higher_warn_value) {
                temp_icon.setImageResource(R.drawable.unormal_temp_icon);
            } else if (maxValue < Contants.lower_warn_value) {
                temp_icon.setImageResource(R.drawable.normal_temp_icon);
            } else {
                temp_icon.setImageResource(R.drawable.normal_temp_icon);
            }
            String max_temp = String.format(context.getResources().getString(R.string.temp_cur_value), String.valueOf(maxValue));
            max_v.setText(max_temp);
            start_v.setText(DateTools.getDateString(dayRecord.getStart_time(), "HH:mm:ss"));
            end_v.setText(DateTools.getDateString(dayRecord.getEnd_time(), "HH:mm:ss"));
            day_id = String.valueOf(dayRecord.getDay_id());
        }
    }

    public View getDetailView() {
        return detailView;
    }

    public void setDetailView(View detailView) {
        this.detailView = detailView;
        presenter.initRoot(detailView, context);
    }

    @Override
    public void loadHistoryRecord() {
        presenter.loadHistoryRecords(day_id);
    }

    @Override
    public void cancelTask() {
        presenter.cancelTask();
    }
}
