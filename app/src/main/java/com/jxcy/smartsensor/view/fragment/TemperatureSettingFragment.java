package com.jxcy.smartsensor.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.adapter.TemeratureAdapter;
import com.jxcy.smartsensor.adapter.WarnEntity;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.view.dialog.WarnDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class TemperatureSettingFragment extends BaseFragment {
    private View rootView;
    private ListView listView;
    private TemeratureAdapter adapter;
    private List<WarnEntity> warnList = new ArrayList<>();
    private WarnDialogFragment warnDialogFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.temperature_fragment_layout, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        listView = root.findViewById(R.id.list_v);
        warnDialogFragment = new WarnDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        warnList.clear();
        String[] warnArray = getResources().getStringArray(R.array.temperature_array);
        for (String warn_value : warnArray) {
            WarnEntity warn = new WarnEntity();
            warn.setWarnDes(warn_value);
            if (warn_value.equals(getResources().getString(R.string.higher_warn_value))) {
                warn.setWarnType(1);
                warn.setWarnValue(Contants.higher_warn_value);
            } else if (warn_value.equals(getResources().getString(R.string.lower_warn_value))) {
                warn.setWarnType(2);
                warn.setWarnValue(Contants.lower_warn_value);
            }
            warnList.add(warn);
        }
        adapter = new TemeratureAdapter(getActivity(), warnList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                warnDialogFragment.setPickerListener(new WarnDialogFragment.PickerListener() {
                    @Override
                    public void itemPicked(String value) {
                        warnList.get(position).setWarnValue(Float.valueOf(value));
                        adapter.notifyDataSetChanged();

                    }
                });
                if(isAdded())
                warnDialogFragment.show(getChildFragmentManager(), "");
            }
        });
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void lazyLoad() {

    }
}
