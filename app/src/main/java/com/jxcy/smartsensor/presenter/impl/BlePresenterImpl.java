package com.jxcy.smartsensor.presenter.impl;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import com.hndw.smartlibrary.Presenter.BaseXPresenter;
import com.hndw.smartlibrary.view.IBaseXView;
import com.jxcy.smartsensor.presenter.BlePresenter;
import com.jxcy.smartsensor.service.LcbAliveService;
import com.jxcy.smartsensor.view.ui.BleConnectUi;

public class BlePresenterImpl extends BaseXPresenter implements BlePresenter {
    private BleConnectUi ui;

    public BlePresenterImpl(@NonNull IBaseXView view) {
        super(view);
        ui = (BleConnectUi) view;
    }
    @Override
    public void startLcbServer() {
        Intent intent = new Intent(ui.getSelfActivity(), LcbAliveService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ui.getSelfActivity().startForegroundService(intent);
        } else {
            ui.getSelfActivity().startService(intent);
        }
    }
}
