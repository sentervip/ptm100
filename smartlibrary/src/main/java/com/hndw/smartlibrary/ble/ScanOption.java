package com.hndw.smartlibrary.ble;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ScanOption {
    List<String> mFilterNames = new ArrayList<>();
    List<String> mFilterAddresses = new ArrayList<>();
    List<String> mSpecifiedNames = new ArrayList<>();
    List<String> mSpecifiedAddresses = new ArrayList<>();
    boolean autoConnect = false;
    int mMinRssi = -99;
    long mPeriod = 5000;

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public ScanOption specifyName(String name, boolean reset) {
        if (reset) mSpecifiedNames.clear();

        if (!TextUtils.isEmpty(name)) {
            mSpecifiedNames.add(name);
        }
        return this;
    }

    public ScanOption specifyName(String name) {
        return specifyName(name, false);
    }

    public ScanOption specifyNames(List<String> names, boolean reset) {
        if (reset) mSpecifiedNames.clear();
        mSpecifiedNames.addAll(names);
        return this;
    }

    public ScanOption specifyNames(List<String> names) {
        return specifyNames(names, false);
    }

    public ScanOption specifyAddress(String address, boolean reset) {
        if (reset) mSpecifiedAddresses.clear();

        if (!TextUtils.isEmpty(address)) {
            mSpecifiedAddresses.add(address);
        }
        return this;
    }

    public ScanOption specifyAddress(String address) {
        return specifyAddress(address, false);
    }

    public ScanOption specifyAddresses(List<String> addresses, boolean reset) {
        if (reset) mSpecifiedAddresses.clear();
        mSpecifiedAddresses.addAll(addresses);
        return this;
    }

    public ScanOption specifyAddresses(List<String> addresses) {
        return specifyAddresses(addresses, false);
    }

    public ScanOption filterName(String name, boolean reset) {
        if (reset) {
            mFilterNames.clear();
        }
        mFilterNames.add(name);
        return this;
    }

    public ScanOption filterName(String name) {
        return filterName(name, false);
    }

    public ScanOption filterName(List<String> names, boolean reset) {
        if (reset) {
            mFilterNames.clear();
        }
        mFilterNames.addAll(names);
        return this;
    }

    public ScanOption filterName(List<String> names) {
        return filterName(names, false);
    }

    public ScanOption filterAddress(String address, boolean reset) {
        if (reset) {
            mFilterAddresses.clear();
        }
        mFilterAddresses.add(address);
        return this;
    }

    public ScanOption filterAddress(String address) {
        return filterAddress(address, false);
    }

    public ScanOption filterAddresses(List<String> addresses, boolean reset) {
        if (reset) {
            mFilterAddresses.clear();
        }
        mFilterAddresses.addAll(addresses);
        return this;
    }

    public ScanOption filterAddresses(List<String> addresses) {
        return filterAddresses(addresses, false);
    }

    public ScanOption minRssi(int rssi) {
        mMinRssi = rssi;
        if (mMinRssi > -64) {
            mMinRssi = -64;
        }
        return this;
    }

    public ScanOption scanPeriod(long period) {
        mPeriod = period;
        if (mPeriod < 1000) {
            mPeriod = 5000;
        }
        return this;
    }
}
