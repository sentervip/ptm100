package com.hndw.smartlibrary.ble;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BleTools {
    public static void refreshBleAppFromSystem(Context context, String packageName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return;
        }
        if (!adapter.isEnabled()) {
            return;
        }
        try {
            Object mIBluetoothManager = getIBluetoothManager(adapter);
            Method isBleAppPresentM = mIBluetoothManager.getClass().getDeclaredMethod("isBleAppPresent");
            isBleAppPresentM.setAccessible(true);
            boolean isBleAppPresent = (Boolean) isBleAppPresentM.invoke(mIBluetoothManager);
            if (isBleAppPresent) {
                return;
            }
            Field mIBinder = BluetoothAdapter.class.getDeclaredField("mToken");
            mIBinder.setAccessible(true);
            Object mToken = mIBinder.get(adapter);

            //刷新偶尔系统无故把app视为非 BLE应用 的错误标识 导致无法扫描设备
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //8.0+ (部分手机是7.1.2 也是如此)
                Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class, String.class);
                updateBleAppCount.setAccessible(true);
                //关一下 再开
                updateBleAppCount.invoke(mIBluetoothManager, mToken, false, packageName);
                updateBleAppCount.invoke(mIBluetoothManager, mToken, true, packageName);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                try {
                    //6.0~7.1.1

                    Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class);
                    updateBleAppCount.setAccessible(true);
                    //关一下 再开
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, false);
                    updateBleAppCount.invoke(mIBluetoothManager, mToken, true);
                } catch (NoSuchMethodException e) {
                    //8.0+ (部分手机是7.1.2 也是如此)
                    try {
                        Method updateBleAppCount = mIBluetoothManager.getClass().getDeclaredMethod("updateBleAppCount", IBinder.class, boolean.class, String.class);
                        updateBleAppCount.setAccessible(true);
                        //关一下 再开
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, false, packageName);
                        updateBleAppCount.invoke(mIBluetoothManager, mToken, true, packageName);
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static final int CONNECTION_STATE_DISCONNECTED = 0;
    public static final int CONNECTION_STATE_CONNECTED = 1;
    public static final int CONNECTION_STATE_UN_SUPPORT = -1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("PrivateApi")
    public static int getInternalConnectionState(String mac) {
        //该功能是在21 (5.1.0)以上才支持, 5.0 以及以下 都 不支持
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return CONNECTION_STATE_UN_SUPPORT;
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("OPPO")) {//OPPO勿使用这种办法判断, OPPO无解
            return CONNECTION_STATE_UN_SUPPORT;
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice remoteDevice = adapter.getRemoteDevice(mac);
        Object mIBluetooth = null;
        try {
            Field sService = BluetoothDevice.class.getDeclaredField("sService");
            sService.setAccessible(true);
            mIBluetooth = sService.get(null);
        } catch (Exception e) {
            return CONNECTION_STATE_UN_SUPPORT;
        }
        if (mIBluetooth == null) return CONNECTION_STATE_UN_SUPPORT;

        boolean isConnected;
        try {
            Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected");
            isConnectedMethod.setAccessible(true);
            isConnected = (Boolean) isConnectedMethod.invoke(remoteDevice);
            isConnectedMethod.setAccessible(false);
        } catch (Exception e) {
            //如果找不到,说明不兼容isConnected, 尝试去使用getConnectionState 判断
            try {
                Method getConnectionState = mIBluetooth.getClass().getDeclaredMethod("getConnectionState", BluetoothDevice.class);
                getConnectionState.setAccessible(true);
                int state = (Integer) getConnectionState.invoke(mIBluetooth, remoteDevice);
                getConnectionState.setAccessible(false);
                isConnected = state == CONNECTION_STATE_CONNECTED;
            } catch (Exception e1) {
                return CONNECTION_STATE_UN_SUPPORT;
            }
        }
        return isConnected ? CONNECTION_STATE_CONNECTED : CONNECTION_STATE_DISCONNECTED;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setLeServiceEnable(boolean isEnable) {

        Object mIBluetooth;
        try {
            Field sService = BluetoothDevice.class.getDeclaredField("sService");
            sService.setAccessible(true);
            mIBluetooth = sService.get(null);
        } catch (Exception e) {
            return;
        }
        if (mIBluetooth == null) return;

        try {
            if (isEnable) {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onLeServiceUp");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            } else {
                Method onLeServiceUp = mIBluetooth.getClass().getDeclaredMethod("onBrEdrDown");
                onLeServiceUp.setAccessible(true);
                onLeServiceUp.invoke(mIBluetooth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean releaseAllScanClient() {
        try {
            Object mIBluetoothManager = getIBluetoothManager(BluetoothAdapter.getDefaultAdapter());
            if (mIBluetoothManager == null) return false;
            Object iGatt = getIBluetoothGatt(mIBluetoothManager);
            if (iGatt == null) return false;

            Method unregisterClient = getDeclaredMethod(iGatt, "unregisterClient", int.class);
            Method stopScan;
            int type;
            try {
                type = 0;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class, boolean.class);
            } catch (Exception e) {
                type = 1;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class);
            }

            for (int mClientIf = 0; mClientIf <= 40; mClientIf++) {
                if (type == 0) {
                    try {
                        stopScan.invoke(iGatt, mClientIf, false);
                    } catch (Exception ignored) {
                    }
                }
                if (type == 1) {
                    try {
                        stopScan.invoke(iGatt, mClientIf);
                    } catch (Exception ignored) {
                    }
                }
                try {
                    unregisterClient.invoke(iGatt, mClientIf);
                } catch (Exception ignored) {
                }
            }
            stopScan.setAccessible(false);
            unregisterClient.setAccessible(false);
            getDeclaredMethod(iGatt, "unregAll").invoke(iGatt);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isScanClientInitialize(ScanCallback callback) {
        try {
            Field mLeScanClientsField = getDeclaredField(BluetoothLeScanner.class, "mLeScanClients");
            //  HashMap<ScanCallback, BleScanCallbackWrapper>()
            HashMap callbackList = (HashMap) mLeScanClientsField.get(BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner());
            int size = callbackList == null ? 0 : callbackList.size();
            if (size > 0) {
                Iterator iterator = callbackList.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    if (val != null && key != null && key == callback) {
                        int mClientIf = 0;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Field mScannerIdField = getDeclaredField(val, "mScannerId");
                            mClientIf = mScannerIdField.getInt(val);

                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Field mClientIfField = getDeclaredField(val, "mClientIf");
                            mClientIf = mClientIfField.getInt(val);
                        }
                        System.out.println("mClientIf=" + mClientIf);
                        return true;
                    }
                }
            } else {
                if (callback != null) {
                    return false;
                }
            }


        } catch (Exception ignored) {

        }
        return true;
    }

    @SuppressLint("PrivateApi")
    public static Object getIBluetoothGatt(Object mIBluetoothManager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothGatt = getDeclaredMethod(mIBluetoothManager, "getBluetoothGatt");
        return getBluetoothGatt.invoke(mIBluetoothManager);
    }


    @SuppressLint("PrivateApi")
    public static Object getIBluetoothManager(BluetoothAdapter adapter) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getBluetoothManager = getDeclaredMethod(BluetoothAdapter.class, "getBluetoothManager");
        return getBluetoothManager.invoke(adapter);
    }


    public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
        Field declaredField = clazz.getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }


    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = clazz.getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }


    public static Field getDeclaredField(Object obj, String name) throws NoSuchFieldException {
        Field declaredField = obj.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }


    public static Method getDeclaredMethod(Object obj, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }
}
