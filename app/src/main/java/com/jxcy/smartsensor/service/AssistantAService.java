package com.jxcy.smartsensor.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.jxcy.smartsensor.ILiveKeppAidlInterface;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.utils.NotificationUtils;


public class AssistantAService extends Service {
    private final String Lcb_PackageName = "com.jxcy.smartsensor.service.service";
    private final String Lcb_ServicePath = "com.jxcy.smartsensor.service.LcbAliveService";
    private ILiveKeppAidlInterface.Stub mBinderFromLcb;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindLuChiBao();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderFromLcb = (ILiveKeppAidlInterface.Stub) ILiveKeppAidlInterface.Stub.asInterface(service);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinderToA;
    }

    private ILiveKeppAidlInterface.Stub mBinderToA = new ILiveKeppAidlInterface.Stub() {
    };

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bindLuChiBao();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void bindLuChiBao() {
        Intent clientIntent = new Intent();
        clientIntent.setClassName(Lcb_PackageName, Lcb_ServicePath);
        startService(clientIntent);
        bindService(clientIntent, conn, Context.BIND_AUTO_CREATE);
    }
}

