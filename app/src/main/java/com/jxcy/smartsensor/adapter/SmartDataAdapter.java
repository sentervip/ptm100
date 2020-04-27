package com.jxcy.smartsensor.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;


public class SmartDataAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> data;

    public SmartDataAdapter(FragmentManager fm, List<Fragment> mlist) {
        super(fm);
        data = mlist;
    }

    public void updateData(List<Fragment> fragments) {
        data = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
    }
}
