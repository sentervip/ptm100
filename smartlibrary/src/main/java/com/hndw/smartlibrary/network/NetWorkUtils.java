package com.hndw.smartlibrary.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *
 * @author ljh
 */
public class NetWorkUtils {
    //判断网络是否连接
    public static boolean isNetWorkAvailable(Context context) {
        //网络连接管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //网络信息
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            return true;
        }
        return false;
    }
}
