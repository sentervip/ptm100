package com.hndw.smartlibrary.view;

import android.app.Activity;

public interface IBaseXView {
    /**
     * 获取 Activity 对象
     *
     * @return activity
     */
    <T extends Activity> T getSelfActivity();
}