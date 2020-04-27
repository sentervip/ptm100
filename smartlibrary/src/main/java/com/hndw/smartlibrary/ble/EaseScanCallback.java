package com.hndw.smartlibrary.ble;

import android.bluetooth.BluetoothDevice;

public interface EaseScanCallback {
    void onDeviceFound(BluetoothDevice device);

    void onBluetoothDisabled();

    void onScanStart(boolean start);
}
