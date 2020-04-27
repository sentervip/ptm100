package com.hndw.smartlibrary.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.until.MyContextWrapper;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.hndw.smartlibrary.until.SystemBarTintUtil;
import com.umeng.analytics.MobclickAgent;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import com.hndw.smartlibrary.until.MyContextWrapper;
//import com.hndw.smartlibrary.until.PreferenceTool;

/**
 * @author ljh
 */
public abstract class BaseActivity<P extends IBaseXPresenter> extends AppCompatActivity implements IBaseXView, Handler.Callback {
    private P mPresenter;
    private List<Activity> taskList = new ArrayList<>();
    public ActivityHander hander;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        fullScreen(this);
        super.onCreate(savedInstanceState);
        SystemBarTintUtil tintUtil = new SystemBarTintUtil(this);
        tintUtil.setNavigationBarAlpha(1.0f);
        taskList.add(this);
        MobclickAgent.setDebugMode(true);
        hander = new ActivityHander(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = checkLocal(newBase);
        super.attachBaseContext(context);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private Context checkLocal(Context newBase) {
        String language = PreferenceTool.getInstance(newBase).getDefaultLanguage();
        Context context = newBase;
        if (language != null && !"".equals(language)) {
            if (language.toLowerCase().equals("zh")) {
                Locale newLocale = Locale.CHINA;
                context = MyContextWrapper.wrap(newBase, newLocale);
            }
        }
        return context;
    }

    @Override
    public boolean handleMessage(Message msg) {

        return false;
    }


    /**
     * 创建 Presenter
     *
     * @return
     */
    public abstract P onBindPresenter();

    /**
     * 获取 Presenter 对象，在需要获取时才创建`Presenter`，起到懒加载作用
     */
    public P getPresenter() {
        if (mPresenter == null) {
            mPresenter = onBindPresenter();
        }
        return mPresenter;
    }

    @Override
    public Activity getSelfActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 在生命周期结束时，将 presenter 与 view 之间的联系断开，防止出现内存泄露
         */
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    protected void exitApplication() {
        for (Activity activity : taskList) {
            activity.finish();
        }
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 通过设置全屏，设置状态栏透明
     *
     * @param activity
     */
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    private static class ActivityHander extends Handler {
        private WeakReference<BaseActivity> reference;

        public ActivityHander(BaseActivity activity) {
            reference = new WeakReference<BaseActivity>(activity);
        }
    }
}
