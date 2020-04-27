package com.hndw.smartlibrary.ble;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Looper;

public abstract class EaseScanner {
    static BluetoothAdapter sBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    protected Handler sHandler = new Handler(Looper.getMainLooper());

    long mPeriod = 5000;
    ScanOption mScanOption;
    EaseScanCallback mEaseScanCallback;
    public volatile boolean isScanning = false;

    private Runnable mStopScanRunnable = new Runnable() {

        @Override
        public void run() {
            startScan(false);
        }
    };

    public abstract void startScan(boolean scan);

    public void exit() {
        startScan(false);
        mEaseScanCallback = null;
    }

    public EaseScanner setmScanOption(ScanOption option) {
        mScanOption = option;
        return this;
    }

    public EaseScanner setmEaseScanCallback(EaseScanCallback mEaseScanCallback) {
        this.mEaseScanCallback = mEaseScanCallback;
        return this;
    }

    void stopScanDelay() {
        sHandler.postDelayed(mStopScanRunnable, mPeriod);
    }

    void removeStop() {
        sHandler.removeCallbacksAndMessages(null);
    }
}
