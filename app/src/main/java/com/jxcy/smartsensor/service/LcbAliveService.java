package com.jxcy.smartsensor.service;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.data.BleScanState;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.hndw.smartlibrary.until.DateTools;
import com.hndw.smartlibrary.until.PreferenceTool;
import com.jxcy.smartsensor.AIDLDemoBinder;
import com.jxcy.smartsensor.ILiveKeppAidlInterface;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.SmartApplication;
import com.jxcy.smartsensor.greendao.DaoSession;
import com.jxcy.smartsensor.greendao.DayDetailRecord;
import com.jxcy.smartsensor.greendao.DayRecord;
import com.jxcy.smartsensor.receiver.ScreenReceiverUtil;
import com.jxcy.smartsensor.utils.Contants;
import com.jxcy.smartsensor.utils.NotificationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.jxcy.smartsensor.utils.Contants.dayRecord;

/**
 * @decrible 保活后台服务，绑定启动保活助手A的服务
 * <p>
 */
public class LcbAliveService extends Service implements Handler.Callback {
    private final String A_PackageName = "com.jxcy.smartsensor.service";
    private final String A_ServicePath = "com.jxcy.smartsensor.service.AssistantAService";
    private ILiveKeppAidlInterface.Stub mBinderFromA;
    private MediaPlayer mMediaPlayer;
    private SmartApplication application;
    private DaoSession daoSession;
    private Context mContext;
    private ScreenReceiverUtil mScreenListener;
    boolean prompt_enable = true;
    boolean warn_enable = true;
    PreferenceTool preferenceTool;
    enum warnType {
        normal, lower, highter
    }

    warnType curWarnMode = warnType.normal;
    warnType olderWarnMode = null;
    int warn_count = 0;
    Ringtone rt;
    NotifyTask notifyTask;
    private float max_value, cur_value, battery;
    Handler handler = new Handler(this);
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bindAliveA();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderFromA = (ILiveKeppAidlInterface.Stub) ILiveKeppAidlInterface.Stub.asInterface(service);
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return mBinderToA;
    }

    private ILiveKeppAidlInterface.Stub mBinderToA = new AIDLDemoBinder.Stub() {
    };

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        application = (SmartApplication) getApplication();
        daoSession = application.getDaoSession();
        preferenceTool = PreferenceTool.getInstance(this);
        EventBus.getDefault().register(this);
        initRing();
        initConfig();
        BleManager.getInstance().setReConnectCount(5, 5 * 1000);
        bindAliveA();
        NotificationUtils.notification(this, getResources().getString(R.string.baby_name), getResources().getString(R.string.cur_temperature), R.drawable.high_low_icon);
        startForeground(NotificationUtils.DEFAULT_NOTIFY_ID, NotificationUtils.notification);
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.silent);
        mMediaPlayer.setLooping(true);
        mScreenListener = new ScreenReceiverUtil(mContext);
        mScreenListener = new ScreenReceiverUtil(this);
        mScreenListener.setScreenReceiverListener(new ScreenReceiverUtil.SreenStateListener() {
            @Override
            public void screenOn() {

            }

            @Override
            public void screenOff() {

            }

            @Override
            public void onUserPresent() {

            }
        });
    }

    private void doScan() {
        String mac_address = PreferenceTool.getInstance(mContext).getStringValue("ble_mac");
        if (mac_address != null && !"".equals(mac_address)) {
            BleManager.getInstance().connect(mac_address, callback);
        } else {
            BleManager.getInstance().scan(new BleScanCallback() {
                @Override
                public void onScanFinished(List<BleDevice> scanResultList) {
                    if (BleManager.getInstance().getScanSate() == BleScanState.STATE_SCANNING) {
                        BleManager.getInstance().cancelScan();
                    }
                    if (scanResultList == null || scanResultList.size() == 0) {
                        doScan();
                    }
                }

                @Override
                public void onScanStarted(boolean success) {
                }

                @Override
                public void onScanning(BleDevice bleDevice) {

                }

                @Override
                public void onLeScan(BleDevice bleDevice) {
                    super.onLeScan(bleDevice);
                    if (bleDevice != null && bleDevice.getName() != null) {
                        if (bleDevice.getName().contains(Contants.deviceName) && Contants.BLE_CONNECT_STATUS != BluetoothProfile.STATE_CONNECTED) {
                            BleManager.getInstance().connect(bleDevice, callback);
                            BleManager.getInstance().cancelScan();
                        }
                    }
                }

            });
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startPlayMusic();
        doScan();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayMusic();
        BleManager.getInstance().cancelScan();
        BleManager.getInstance().disconnect(Contants.bleDevice);
        BleManager.getInstance().destroy();
        EventBus.getDefault().unregister(this);
        reConnectCount = 0;
        mScreenListener.stopScreenReceiverListener();
        dayRecord = null;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void bindAliveA() {
        Intent serverIntent = new Intent();
        serverIntent.setClassName(A_PackageName, A_ServicePath);
        startService(serverIntent);
        bindService(serverIntent, conn, Context.BIND_AUTO_CREATE);
    }

    private void startPlayMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void stopPlayMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    /**
     * 初始化蓝牙连接
     */
    private void initConfig() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(null)      // 只扫描指定的服务的设备，可选
                .setDeviceName(true, Contants.deviceName)         // 只扫描指定广播名的设备，可选
                // .setDeviceMac(Contants.macAddress)                  // 只扫描指定mac的设备，可选
                .setAutoConnect(Contants.AUTO_CONNECT)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(Contants.CONNECT_OUT_TIME)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private void initRing() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            rt.setLooping(true);
            if (Contants.cur_sound == Contants.sound_strong) {
                rt.setVolume(1.0f);
            } else if (Contants.cur_sound == Contants.sound_week) {
                rt.setVolume(0.1f);
            } else {
                rt.setVolume(0.5f);
            }
        }
    }

    class NotifyTask extends Thread {
        @Override
        public void run() {
            super.run();
            if (Contants.bleDevice == null) return;
            BleManager.getInstance().notify(Contants.bleDevice, Contants.SERVER_UUID.toLowerCase(), Contants.THERMOMETER_UUID.toLowerCase(), new BleNotifyCallback() {
                @Override
                public void onNotifySuccess() {
                    Contants.rssi = Contants.bleDevice.getRssi();
                    updateCurBattery();
                    validTime = System.currentTimeMillis();
                }

                @Override
                public void onNotifyFailure(BleException exception) {

                }

                @Override
                public void onCharacteristicChanged(byte[] data) {
                    try {
                        if (data != null && data.length > 0 && Contants.curBaby != null) {
                            cur_value = Float.valueOf(new String(data)) + Contants.adjust_temperature;
                            BigDecimal bigDecimal = new BigDecimal(cur_value);
                            cur_value = bigDecimal.setScale(2, RoundingMode.HALF_UP).floatValue();
                            MessageEvent curEvent = new MessageEvent(Contants.BLE_CURRENT_VALUE_KEY, cur_value);
                            EventBus.getDefault().post(curEvent);
                            String vString = String.valueOf(cur_value);
                            if (vString.length() == 4) {
                                vString += "0";
                            }
                            if (Contants.curBaby != null) {
                                NotificationUtils.notification(mContext, Contants.curBaby.getNickName(),
                                        String.format(getResources().getString(R.string.cur_temperature), vString), R.drawable.high_low_icon);
                            }
                            if (cur_value > Contants.maxValue || cur_value < Contants.minValue) {
                                return;
                            } else {
                                if (cur_value > max_value) {
                                    max_value = cur_value;
                                }
                                curWarnMode = warnType.normal;
                                insertMeasureValue();
                            }
                            if ((cur_value >= Contants.higher_warn_value || cur_value <= Contants.lower_warn_value) && rt != null) {
                                if (cur_value > Contants.higher_warn_value) {
                                    curWarnMode = warnType.highter;
                                } else if (cur_value < Contants.lower_warn_value) {
                                    curWarnMode = warnType.lower;
                                }
                                if (olderWarnMode != curWarnMode) {
                                    olderWarnMode = curWarnMode;
                                    warn_count = 0;
                                    preferenceTool.editBoolean("done_key", false);
                                }
                                if (!warn_enable) {
                                    if (warn_count >= 30) {
                                        warn_enable = true;
                                        warn_count = 0;
                                        preferenceTool.editBoolean("done_key", false);
                                    }
                                    warn_count++;
                                }
                                if (!rt.isPlaying() && System.currentTimeMillis() - validTime > (2 * 60 * 1000)) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        if (Contants.cur_sound == Contants.sound_strong) {
                                            rt.setVolume(1.0f);
                                        } else if (Contants.cur_sound == Contants.sound_week) {
                                            rt.setVolume(0.1f);
                                        } else {
                                            rt.setVolume(0.5f);
                                        }
                                    }
                                    if (warn_enable && !rt.isPlaying()) {
                                        rt.play();
                                    }
                                }
                            } else if (rt != null && (cur_value < Contants.higher_warn_value && cur_value > Contants.lower_warn_value)) {
                                if (rt.isPlaying()) {
                                    rt.stop();
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        }
    }

    private BleGattCallback callback = new BleGattCallback() {
        @Override
        public void onStartConnect() {
            if (Contants.bleDevice != null) {
                MessageEvent connectingEvent = new MessageEvent(Contants.BLE_CONNNECTING_KEY, null);
                EventBus.getDefault().post(connectingEvent);
            }
        }

        @Override
        public void onConnectFail(BleDevice bleDevice, BleException exception) {
            MessageEvent failEvent = new MessageEvent(Contants.BLE_CONNECT_FAIL_KEY, null);
            EventBus.getDefault().post(failEvent);
            if (Contants.AUTO_CONNECT) {
                if (dayRecord != null && reConnectCount > 6) {
                    daoSession.getDayRecordDao().update(dayRecord);
                    dayRecord = null;
                    Log.i("ljh", "更新结束时间");
                }
                if (!handler.hasMessages(startConnectMsg)) {
                    handler.sendEmptyMessage(startConnectMsg);
                }
            }
        }

        @Override
        public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
            MessageEvent successEvent = new MessageEvent(Contants.BLE_CONNECT_SUCCESS_KEY, null);
            EventBus.getDefault().post(successEvent);
            Contants.bleDevice = bleDevice;
            PreferenceTool.getInstance(mContext).editString("ble_mac", bleDevice.getMac());
            Contants.BLE_CONNECT_STATUS = status;
            handler.sendEmptyMessage(hasConnectMsg);


        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
            Contants.BLE_CONNECT_STATUS = status;
            handler.sendEmptyMessageDelayed(disConnectMsg, 6 * 1000);
            MessageEvent disConnectEvent = new MessageEvent(Contants.BLE_DISCONNNECT_KEY, null);
            EventBus.getDefault().post(disConnectEvent);
            handler.sendEmptyMessageDelayed(warnMsg, 2 * 1000 * 60);
        }
    };

    private final int hasConnectMsg = 0x01;
    private final int disConnectMsg = 0x02;
    private final int startConnectMsg = 0x03;
    private int reConnectCount = 0x00;
    private final int warnMsg = 0x04;
    private long validTime;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case hasConnectMsg:
                handler.removeMessages(disConnectMsg);
                handler.removeMessages(warnMsg);
                if (notifyTask != null) {
                    notifyTask.interrupt();
                    notifyTask = null;
                }
                notifyTask = new NotifyTask();
                notifyTask.start();
                reConnectCount = 0;
                break;
            case disConnectMsg:
                if (notifyTask != null) {
                    notifyTask.interrupt();
                    notifyTask = null;
                }
                if (!handler.hasMessages(startConnectMsg)) {
                    handler.sendEmptyMessage(startConnectMsg);
                }
                break;
            case warnMsg:
                if (!rt.isPlaying() && prompt_enable) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        rt.setVolume(1.0f);
                    }
                    rt.play();

                }
                break;
            case startConnectMsg:
                if (Contants.bleDevice == null) {
                    MessageEvent curEvent = new MessageEvent(Contants.BLE_WAIT_CONNECT_KEY, null);
                    EventBus.getDefault().post(curEvent);
                    doScan();
                    break;
                }
                if (Contants.AUTO_CONNECT) {
                    if (Contants.bleDevice != null) {
                        BleManager.getInstance().disconnect(Contants.bleDevice);
                        BleManager.getInstance().connect(Contants.bleDevice, callback);
                        reConnectCount++;
                    } else {
                        doScan();
                    }
                }
                break;
        }
        return false;
    }

    Timer timer;

    public void updateCurBattery() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BleManager.getInstance().read(Contants.bleDevice, Contants.SERVER_UUID, Contants.BATTER_UUID, new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        battery = Float.valueOf(new String(data)) + Contants.adjust_temperature;
                        BigDecimal bigDecimal = new BigDecimal(battery);
                        battery = bigDecimal.setScale(2, RoundingMode.HALF_UP).floatValue();
                        Log.i("ljh", "current battery = " + battery);
                        MessageEvent curEvent = new MessageEvent(Contants.BLE_CURRENT_BATTERY_KEY, battery);
                        EventBus.getDefault().post(curEvent);
                    }

                    @Override
                    public void onReadFailure(BleException exception) {

                    }
                });
            }
        }, 0, 5 * 60 * 1000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doEventMessage(MessageEvent event) {
        if (event != null) {
            if (event.getMessageKey() == Contants.PROMPT_ENABLE_KEY) {
                prompt_enable = (boolean) event.getObject();
                if (rt.isPlaying() && !prompt_enable) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        rt.setVolume(1.0f);
                    }
                    rt.stop();
                }
                if (!rt.isPlaying() && prompt_enable) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        rt.setVolume(1.0f);
                    }
                    rt.play();
                }
            } else if (event.getMessageKey() == Contants.WARN_ENABLE_KEY) {
                warn_enable = (boolean) event.getObject();
                if (rt.isPlaying() && !warn_enable) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        rt.setVolume(1.0f);
                    }
                }
                rt.stop();
            }
        }
    }

    /**
     * 数据持久化
     */
    private void insertMeasureValue() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (Contants.curBaby == null) return;
                String dateString = DateTools.getCurDayString(null);
                BigDecimal bigDecimal = new BigDecimal(max_value);
                max_value = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                if (dayRecord != null) {
                    dayRecord.setMaxTemperature(max_value);
                    daoSession.getDayRecordDao().update(dayRecord);
                } else {
                    dayRecord = new DayRecord();
                    dayRecord.setCurDate(dateString);
                    dayRecord.setDay_id(null);
                    dayRecord.setBaby_id(Contants.curBaby.getId());
                    dayRecord.setStart_time(System.currentTimeMillis());
                    daoSession.getDayRecordDao().insert(dayRecord);
                }
                if (dayRecord != null) {
                    DayDetailRecord detailRecord = new DayDetailRecord();
                    detailRecord.setRecord_id(null);
                    detailRecord.setDay_id(dayRecord.getDay_id());
                    detailRecord.setInsertTime(DateTools.getCurDayTime().getTime());
                    BigDecimal curDecimal = new BigDecimal(cur_value);
                    cur_value = curDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    detailRecord.setTemperature(cur_value);
                    daoSession.getDayDetailRecordDao().insert(detailRecord);
                    dayRecord.setEnd_time(System.currentTimeMillis());
                    daoSession.getDayRecordDao().update(dayRecord);
                }
            }
        }.start();
    }
}
