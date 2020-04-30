package com.jxcy.smartsensor.utils;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;

import com.clj.fastble.data.BleDevice;
import com.jxcy.smartsensor.greendao.BabyEntity;
import com.jxcy.smartsensor.greendao.DayRecord;

/**
 * 全局常量
 * <p>
 * Created by ljh on 2019/8/18.
 */

public class Contants {
    public static final boolean DEBUG = true;
    public static final String DB_NAME = "sensor.db";
    /**
     * 当前baby
     */
    public static BabyEntity curBaby;
    /**
     * 包路径
     */
    public static final String PACKAGE_NAME = "com.jxcy.smartsensor";

    public static final String SOUND_UNIT = "sound_unit";
    public static final String UNIT_VALUE = "unit_value";
    public static BleDevice bleDevice;
    public static BluetoothGatt bluetoothGatt;
    public static final String SERVER_UUID = "EDFEC62E-9910-0BAC-5241-D8BDA6932A2F";
    public static final String DEVICE_INFO_SERVER_UUID = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String SOFT_VERSION_UUID = "00002a28-0000-1000-8000-00805f9b34fb";

    public static final String THERMOMETER_UUID = "15005991-B131-3396-014C-664C9867B917";
    public static final String BATTER_UUID = "6EB675AB-8BD1-1B9A-7444-621E52EC6823";
    public static final String deviceName = "PTM";
    public static final String macAddress = "80:EA:CA:00:00:07";
    public static int BLE_CONNECT_STATUS = BluetoothProfile.STATE_DISCONNECTED;
    public static int CONNECT_OUT_TIME = 10 * 1000;
    public static boolean AUTO_CONNECT = true;

    public static String BLE_CONNECT_SUCCESS_KEY = "connect_success_key";
    public static String BLE_CONNECT_FAIL_KEY = "connect_fail_key";
    public static String BLE_DISCONNNECT_KEY = "ble_disconnect";
    public static String BLE_CONNNECTING_KEY = "ble_connecting";
    public static String CUR_BABY_UPDATE_KEY = "cur_baby_update_key";
    public static String BLE_CURRENT_VALUE_KEY = "current_temperature";
    public static String BLE_CURRENT_BATTERY_KEY = "current_battery";
    public static String BLE_WAIT_CONNECT_KEY = "ble_wait_connect_key";
    public static String PROMPT_ENABLE_KEY = "prompt_enable_key";

    public static String WARN_ENABLE_KEY = "warn_enable_key";

    public static String HIGHER_WARN_SOUND = "higher_warn_sound";
    public static String LOWER_WARN_SOUND = "lower_warn_sound";

    public static float higher_warn_value = 38.0f;
    public static float lower_warn_value = 34.0f;
    public static float adjust_temperature = 0.0f;

    public static final float maxValue = 40.0f;
    public static final float minValue = 34.0f;

    public static final int sound_strong = 1;
    public static final int sound_normal = 2;
    public static final int sound_week = 3;

    public static int cur_sound = 2;
    public static int cur_unit = 0;

    public static final int unit_1 = 0;
    public static final int unit_2 = 1;

    public static DayRecord dayRecord;

    public static int rssi;

    public static final float maxBattery = 3.3f;
    public static final float minBattery = 2.5f;


}
