package com.hndw.smartlibrary.ble;

public class BleEvent extends MessageEvent {
    public static final String CHARACTERISTIC_NOTIFY_KEY = "characteristic_notify_key";
    public static final String CHARACTERISTIC_READ_KEY = "characteristic_read_key";
    public static final String CHARACTERISTIC_WRITE_KEY = "characteristic_write_key";

    public BleEvent(String key, Object value) {
        super(key, value);
    }
}
