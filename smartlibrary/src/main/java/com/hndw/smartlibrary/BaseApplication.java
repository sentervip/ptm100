package com.hndw.smartlibrary;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

//import com.hndw.smartlibrary.until.PreferenceTool;
//import com.hndw.smartlibrary.until.PreferenceTool;
import com.hndw.smartlibrary.until.CrashHandler;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class BaseApplication extends Application {
    public PreferenceTool tool;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        tool = PreferenceTool.getInstance(this);
       /* if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        refWatcher = LeakCanary.install(this);*/
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }
}
