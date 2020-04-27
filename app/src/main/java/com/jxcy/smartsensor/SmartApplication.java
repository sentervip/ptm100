package com.jxcy.smartsensor;

import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.clj.fastble.BleManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hndw.smartlibrary.BaseApplication;
import com.jxcy.smartsensor.greendao.DaoMaster;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.utils.Contants;

import org.greenrobot.greendao.database.Database;

public class SmartApplication extends BaseApplication {
    private DaoSession daoSession;
    private LocationService locationService;

    public LocationService getLocationService() {
        return locationService;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        locationService = new LocationService(this);
        SDKInitializer.initialize(this);
        // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initGreenDao();
    }


    private void initGreenDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, Contants.DB_NAME);
        Database db = helper.getEncryptedWritableDb("20190815");
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

/*        DaoMaster.DevOpenHelper dbHelper = new DaoMaster.DevOpenHelper(new MyContextWrapper(this), Contants.DB_NAME, null);
        Database db = dbHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();*/
    }

    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            if (Contants.bleDevice != null)
                BleManager.getInstance().disconnect(Contants.bleDevice);
            BleManager.getInstance().destroy();
        }
    };
}
