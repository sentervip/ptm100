package com.jxcy.smartsensor.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseFragment;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.adapter.HistoryAdapter;
import com.jxcy.smartsensor.adapter.ListViewDivider;
import com.jxcy.smartsensor.greendao.DayRecord;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetailFragment extends BaseFragment {
    private TextView day_v, max_v;
    private View root;
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private DayRecord maxDay;
    List<DayRecord> recordList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.history_detail_fragment_layout, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        recyclerView = root.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListViewDivider(root.getContext(), LinearLayoutManager.VERTICAL));
        adapter = new HistoryAdapter(getContext(), recordList);
        recyclerView.setAdapter(adapter);
        max_v = root.findViewById(R.id.max_v);
        day_v = root.findViewById(R.id.day_title);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null) {
            List<DayRecord> data = bundle.getParcelableArrayList("dayList");
            if (data != null && data.size() > 0) {
                recordList = data;
                adapter.updateHistory(data);
            }
            maxDay = bundle.getParcelable("cur_day");
            if (maxDay != null) {
                day_v.setText(maxDay.getCurDate());
                float maxValue = maxDay.getMaxTemperature();
                String max_temp = String.format(getResources().getString(R.string.temp_cur_value), String.valueOf(maxValue));
                max_v.setText(max_temp);
            }
        }
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
