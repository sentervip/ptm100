package com.jxcy.smartsensor.presenter.impl;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.presenter.HistoryPresenter;
import com.jxcy.smartsensor.utils.QueryKit;
import com.jxcy.smartsensor.view.unit.Temperature;
import com.jxcy.smartsensor.view.unit.TemperatureLineView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryPresenterImpl implements HistoryPresenter {
    private View rootView;
    private TemperatureLineView lineView;
    private QueryKit queryKit;
    private DaoSession daoSession;
    private List<LoadTask> taskArray = new ArrayList<>();

    @Override
    public void initRoot(View root, Context context) {
        daoSession = ((SmartApplication) context.getApplicationContext()).getDaoSession();
        rootView = root;
        lineView = rootView.findViewById(R.id.temperature_v);
        queryKit = new QueryKit(context, daoSession);
    }


    class LoadTask extends AsyncTask<String, Void, List<Temperature>> {
        @Override
        protected List<Temperature> doInBackground(String... strings) {
            return queryKit.queryHistoryList(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Temperature> temperatureList) {
            super.onPostExecute(temperatureList);
            int len = temperatureList.size();
            if (temperatureList != null && len > 0) {
                String firstLabel = temperatureList.get(0).getDateString();
                String endLabel = temperatureList.get(len - 1).getDateString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                long first_time = 0;
                long end_time = 0;
                List<String> xLabels = new ArrayList<>();
                try {
                    Date s_date = dateFormat.parse(firstLabel);
                    Date e_date = dateFormat.parse(endLabel);
                    first_time = s_date.getTime();
                    end_time = e_date.getTime();
                    float m_time = (end_time - first_time) / (15.0f*60*1000);
                    BigDecimal bigDecimal = new BigDecimal(m_time);
                    m_time = bigDecimal.setScale(1, RoundingMode.HALF_UP).floatValue();
                    for (int j = 0; j <= 15; j++) {
                        long cur_time = (long) (first_time + 1000 * 60 * j * m_time);
                        String xValue = dateFormat.format(cur_time);
                        xLabels.add(xValue);
                    }
                    if (xLabels.size() > 0) {
                        lineView.addNormalXLabels(xLabels);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                lineView.setTemperature(temperatureList);
            }
        }

    }

    @Override
    public void loadHistoryRecords(String key) {
        LoadTask loadTask = new LoadTask();
        loadTask.execute(key);
        taskArray.add(loadTask);
    }

    @Override
    public void cancelTask() {
        if (taskArray != null && taskArray.size() > 0) {
            for (int m = 0; m < taskArray.size(); m++) {
                taskArray.get(m).cancel(true);
            }
            taskArray.clear();
        }
    }
}
