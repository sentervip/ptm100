package com.hndw.smartlibrary.ble;

import android.os.Build;

public class ScannerFactory {

    public static EaseScanner createScanner() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new ScannerOld();
        } else {
            return new ScannerNew();
        }
    }
}
