package com.jxcy.smartsensor.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.CrashHandler;
import com.hndw.smartlibrary.view.BaseActivity;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.greendao.BabyEntity;
import com.jxcy.smartsensor.presenter.BlePresenter;
import com.jxcy.smartsensor.presenter.impl.BlePresenterImpl;
import com.jxcy.smartsensor.service.MessageEvent;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.view.ui.BleConnectUi;
import com.smarx.notchlib.INotchScreen;
import com.smarx.notchlib.NotchScreenManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class BleConnectActivity extends BaseActivity implements View.OnClickListener, BleConnectUi {
    private ImageView ble_icon;
    private Button ble_btn;
    private TextView con_tip;
    private BlePresenter blePresenter;
    SmartApplication application;
    Animation rotateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connect_layout);
        con_tip = (TextView) findViewById(R.id.con_tip);
        NotchScreenManager.getInstance().setDisplayInNotch(this);
        // 获取刘海屏信息
        NotchScreenManager.getInstance().getNotchInfo(this, new INotchScreen.NotchScreenCallback() {
            @Override
            public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
                if (notchScreenInfo.hasNotch) {
                    for (Rect rect : notchScreenInfo.notchRects) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) con_tip.getLayoutParams();
                        layoutParams.topMargin = con_tip.getTop() + rect.top;
                        con_tip.setLayoutParams(layoutParams);
                    }
                }
            }
        });
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        BleManager.getInstance().init(getApplication());
        ble_icon = findViewById(R.id.ble_con_icon);
        ble_btn = findViewById(R.id.ble_btn);
        ble_btn.setOnClickListener(this);
        application = (SmartApplication) getApplication();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
        checkGPSIsOpen();
        BleManager.getInstance().enableBluetooth();
        getPresenter();
        if (!BleManager.getInstance().isSupportBle()) {
            exitApplication();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ble_btn:
                blePresenter.startLcbServer();
                doConnectAnim();
                break;
        }
    }

    private void doConnectAnim() {
        rotateAnimation = AnimationUtils.loadAnimation(BleConnectActivity.this, R.anim.rotate_anim);
        LinearInterpolator interpolator = new LinearInterpolator();
        rotateAnimation.setInterpolator(interpolator);
        ble_icon.startAnimation(rotateAnimation);
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        blePresenter = new BlePresenterImpl(this);
        return (IBaseXPresenter) blePresenter;
    }

    private void gotoMainActivity() {
        ArrayList<BabyEntity> babyList = (ArrayList<BabyEntity>) application.getDaoSession().getBabyEntityDao().loadAll();
        if (babyList != null && babyList.size() > 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putParcelableArrayListExtra("baby_array", babyList);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, AddBabyActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEventMessage(MessageEvent event) {
        if (event != null) {
            if (event.getMessageKey().equals(Contants.BLE_CONNECT_SUCCESS_KEY)) {
                gotoMainActivity();
            } else if (event.getMessageKey().equals(Contants.BLE_CONNECT_FAIL_KEY)) {
                if (rotateAnimation != null) {
                    rotateAnimation.cancel();
                }
                Toast.makeText(this, "蓝牙连接失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @TargetApi(23)
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    private final int SDK_PERMISSION_REQUEST = 127;

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SDK_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(BleConnectActivity.this)
                            .setMessage(R.string.storage_permissions_remind)
                            .setPositiveButton(getResources().getString(R.string.done_submit), (dialog1, which) ->
                                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1))
                            .setNegativeButton(getResources().getString(R.string.cancel), null)
                            .create()
                            .show();
                }
            }
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
}
