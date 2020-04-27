package com.hndw.smartlibrary.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.hndw.smartlibrary.until.PreferenceTool;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BleConnector {
    private BluetoothGatt mGatt;
    private EaseScanner easeScanner;
    private ScanOption option;
    private Context context;
    private boolean isConnecting = false;
    private AtomicBoolean isReadOrWriting = new AtomicBoolean(false);
    public static BleConnector connector;
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SERVICE = "";
    public static final String UUID_CHARACTERISTIC = "";
    public static final String UUID_DESC_NOTITY = "";
    public static final String UUID_CHAR_READ = "";
    public static final String UUID_CHAR_WRITE = "";

    public BleConnector(Context context, ScanOption option) {
        this.context = context;
        this.option = option;
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public static BleConnector getInstance(Context context, ScanOption option) {
        if (connector == null) {
            synchronized (BleConnector.class) {
                connector = new BleConnector(context, option);
            }
        }
        return connector;
    }

    /**
     * 蓝牙连接
     */
    public void connectBle() {
        if (isConnecting) return;
        easeScanner = ScannerFactory.createScanner();
        easeScanner.setmScanOption(option);
        easeScanner.setmEaseScanCallback(new EaseScanCallback() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                String mac_address = PreferenceTool.getInstance(context).getStringValue("mac_address");
                if (mac_address != null && !"".equalsIgnoreCase(mac_address)) {
                    BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac_address);
                    bluetoothDevice.connectGatt(context, false, callback);
                } else {
                    if (device != null) {
                        mGatt = device.connectGatt(context, false, callback);
                    }
                }
            }

            @Override
            public void onBluetoothDisabled() {

            }

            @Override
            public void onScanStart(boolean start) {

            }
        });
        easeScanner.startScan(true);
        isConnecting = true;
    }


    BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                    PreferenceTool.getInstance(context).editString("mac_address", gatt.getDevice().getAddress());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                disConnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            notifyData(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            isReadOrWriting.set(false);
            if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_CHARACTERISTIC)) {
                BleEvent notifyEvent = new BleEvent(BleEvent.CHARACTERISTIC_READ_KEY, null);
                EventBus.getDefault().post(notifyEvent);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            isReadOrWriting.set(false);
            if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_CHARACTERISTIC)) {
                BleEvent notifyEvent = new BleEvent(BleEvent.CHARACTERISTIC_WRITE_KEY, null);
                EventBus.getDefault().post(notifyEvent);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (characteristic.getUuid().toString().equalsIgnoreCase(UUID_CHARACTERISTIC)) {
                BleEvent notifyEvent = new BleEvent(BleEvent.CHARACTERISTIC_NOTIFY_KEY, null);
                EventBus.getDefault().post(notifyEvent);
            }
        }
    };

    public void disConnect() {
        if (mGatt != null) {
            mGatt.close();
            mGatt.disconnect();
        }
        refreshDeviceCache();
        isConnecting = false;
    }

    /*    注意：
        1.每次读写数据最多20个字节，如果超过，只能分包
        2.连续频繁读写数据容易失败，读写操作间隔最好200ms以上，或等待上次回调完成后再进行下次读写操作！*/
    public synchronized void read() {
        if (isReadOrWriting.get()) return;
        BluetoothGattService service = mGatt.getService(UUID.fromString(UUID_SERVICE));
        if (service != null) {
            //通过UUID获取可读的Characteristic
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_CHAR_READ));
            //官方建议有通知服务先关闭
            mGatt.setCharacteristicNotification(characteristic, false);
            mGatt.readCharacteristic(characteristic);
        }
        isReadOrWriting.set(true);
    }

    // 写入数据成功会回调->onCharacteristicWrite()
    public synchronized void write(byte[] data, BluetoothGatt mGatt) {
        BluetoothGattService service = mGatt.getService(UUID.fromString(UUID_SERVICE));
        if (service != null) {
            //通过UUID获取可写的Characteristic
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(UUID_CHAR_WRITE));
            characteristic.setValue(data); //单次最多20个字节
            mGatt.writeCharacteristic(characteristic);
        }
    }

    /**
     * 设置自动获取notify
     *
     * @param mGatt
     */
    public void notifyData(BluetoothGatt mGatt) {
        try {
            BluetoothGattService notifyService = mGatt.getService(UUID.fromString(UUID_SERVICE));
            BluetoothGattCharacteristic characteristic = notifyService.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC));
            mGatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(UUID_DESC_NOTITY));
            if (descriptor == null) {
                descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
            }
            if (null != descriptor) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mGatt.writeDescriptor(descriptor);
            }
            mGatt.setCharacteristicNotification(characteristic, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新缓存
     *
     * @return
     */
    private boolean refreshDeviceCache() {
        try {
            Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                boolean success = (boolean) refresh.invoke(mGatt);
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
