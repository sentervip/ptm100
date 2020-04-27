package com.jxcy.smartsensor.presenter;

import android.content.Context;
import android.view.View;

public interface HistoryPresenter {
    void initRoot(View root, Context context);
    void cancelTask();
    void loadHistoryRecords(String key);
}
