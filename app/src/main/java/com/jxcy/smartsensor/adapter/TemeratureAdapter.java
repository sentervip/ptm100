package com.jxcy.smartsensor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hndw.smartlibrary.until.PreferenceTool;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.utils.Contants;

import java.util.List;

public class TemeratureAdapter extends BaseAdapter {
    private List<WarnEntity> warnArray;
    private Context mContext;
    private PreferenceTool preferenceTool;
    public TemeratureAdapter(Context context, List<WarnEntity> warnList) {
        mContext = context;
        warnArray = warnList;
        preferenceTool = PreferenceTool.getInstance(context);
    }

    @Override
    public int getCount() {
        return warnArray != null ? warnArray.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return warnArray != null ? warnArray.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.temperature_item_layout, parent, false);
            viewHold = new ViewHold();
            viewHold.warnIcon = convertView.findViewById(R.id.warn_icon);
            viewHold.itemName = convertView.findViewById(R.id.warn_item);
            viewHold.itemValue = convertView.findViewById(R.id.warn_value);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        WarnEntity warnEntity = warnArray.get(position);
        if (warnEntity.getWarnType() == 1) {
            viewHold.warnIcon.setImageResource(R.drawable.higher_warn_icon);
            Contants.higher_warn_value = warnEntity.getWarnValue();
            preferenceTool.editFloat("higher_ware",Contants.higher_warn_value);
        } else if (warnEntity.getWarnType() == 2) {
            viewHold.warnIcon.setImageResource(R.drawable.lower_warn_icon);
            Contants.lower_warn_value = warnEntity.getWarnValue();
            preferenceTool.editFloat("lower_ware",Contants.lower_warn_value);
        }
        viewHold.itemName.setText(warnEntity.getWarnDes());
        viewHold.itemValue.setText(String.valueOf(warnEntity.getWarnValue()));
        return convertView;
    }

    class ViewHold {
        ImageView warnIcon;
        TextView itemName;
        TextView itemValue;
    }
}