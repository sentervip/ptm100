package com.hndw.smartlibrary.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;

/*
import com.hndw.smartlibrary.until.MyContextWrapper;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.hndw.smartlibrary.until.SystemBarTintUtil;
*/

/**
 * @author ljh
 */
public abstract class BaseFragment<P extends IBaseXPresenter> extends Fragment implements IBaseXView, Handler.Callback {
    private P mPresenter;

    /** Fragment当前状态是否可见 */
    protected boolean isVisible;

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
        return getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 在生命周期结束时，将 presenter 与 view 之间的联系断开，防止出现内存泄露
         */
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    public abstract boolean onBackPressed();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 不可见
     */
    protected void onInvisible() {
    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();
}
