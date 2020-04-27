package com.hndw.smartlibrary.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;

class ScannerOld extends EaseScanner {
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    @Override
    public void startScan(boolean scan) {
        if (mEaseScanCallback == null) return;
        if (isScanning == scan) return;
        if (scan) {
            if (!sBluetoothAdapter.isEnabled()) {
                mEaseScanCallback.onBluetoothDisabled();
                return;
            }
            if (mLeScanCallback == null) {
                mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

                    @Override
                    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
                        if (device == null) return;
                        String name = device.getName();
                        String address = device.getAddress();
                        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address)) return;
                        if (mScanOption != null) {
                            if (!mScanOption.mFilterNames.contains(name)) return;
                            if (!mScanOption.mFilterAddresses.contains(address)) return;
                            if (mScanOption.mSpecifiedNames.size() > 0 && !mScanOption.mSpecifiedNames.contains(name))
                                return;
                            if (mScanOption.mSpecifiedAddresses.size() > 0 && !mScanOption.mSpecifiedAddresses.contains
                                    (address)) return;
                            if (mScanOption.mMinRssi > rssi) return;
                        }

                        sHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (mEaseScanCallback == null) return;
                                if (!isScanning) return;
                                mEaseScanCallback.onDeviceFound(device);
                            }
                        });
                    }
                };
            }

            if (mScanOption != null) {
                mPeriod = mScanOption.mPeriod;
            }
            stopScanDelay();
            sBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            removeStop();
            sBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        isScanning = scan;
        mEaseScanCallback.onScanStart(scan);
    }
}
