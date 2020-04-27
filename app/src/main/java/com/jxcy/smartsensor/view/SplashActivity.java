package com.jxcy.smartsensor.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseActivity;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.Contants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SplashActivity extends BaseActivity {
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_layout);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        handler = new Handler(this);
        handler.sendEmptyMessageDelayed(disConnect, 2000);
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    private final int hasConnect = 0x01;
    private final int disConnect = 0x02;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case hasConnect:
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                break;
            case disConnect:
                startActivity(new Intent(SplashActivity.this, BleConnectActivity.class));
                break;
        }
        finish();
        return super.handleMessage(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEventMessage(MessageEvent event) {
        if (event != null) {
            if (event.getMessageKey().equals(Contants.BLE_CONNECT_SUCCESS_KEY) || event.getMessageKey().equals(Contants.BLE_CURRENT_VALUE_KEY)) {
                handler.removeMessages(disConnect);
                handler.sendEmptyMessage(hasConnect);
            } else if (event.getMessageKey().equals(Contants.BLE_DISCONNNECT_KEY)) {
                handler.sendEmptyMessage(disConnect);
            }
        }
    }
}
