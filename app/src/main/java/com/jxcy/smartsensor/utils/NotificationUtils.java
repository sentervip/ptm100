package com.jxcy.smartsensor.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.jxcy.smartsensor.view.MainActivity;

public class NotificationUtils {
    private static final String DEFAULT_CHANNEL_ID = "default_id";
    private static final String DEFAULT_CHANNEL_NAME = "default_channel_name";
    public static final int DEFAULT_NOTIFY_ID = 1;
    public static Notification notification;

    /**
     * @param context
     * @param title
     * @param content
     * @param smallIcon 显示在状态栏上的小图标，必需
     */
    public static void notification(Context context, String title, String content, int smallIcon) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification_api_26_or_Above(notificationManager, DEFAULT_CHANNEL_ID);
        }
        int notifyID = DEFAULT_NOTIFY_ID;
        String CHANNEL_ID = DEFAULT_CHANNEL_ID;
        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(null)
                .setVibrate(null)
                .setOnlyAlertOnce(true)
                .setWhen(SystemClock.currentThreadTimeMillis())
                .setSmallIcon(smallIcon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }
        notification = builder.build();
        Intent in = new Intent(context, MainActivity.class);// 点击跳转到指定页面
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, in,
                0);
        notification.contentIntent = pIntent;
        notificationManager.notify(notifyID, notification);
    }

    @TargetApi(26)
    private static void notification_api_26_or_Above(NotificationManager notificationManager, String channelId) {
        if (notificationManager == null && TextUtils.isEmpty(channelId)) {
            return;
        }
        String id = channelId;
        String name = DEFAULT_CHANNEL_NAME;
        String description = "我就是一个通知的channel";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = createDefaultChannel(id, name, description, importance);
        notificationManager.createNotificationChannel(channel);
    }

    @TargetApi(26)
    private static NotificationChannel createDefaultChannel(String channelId, String channelName, String desc, int imp) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, imp);
        channel.setDescription(desc);
        //是否使用指示灯
        channel.enableLights(true);
        //指示灯颜色，这个取决于设备是否支持
        channel.setLightColor(Color.RED);
        //是否使用震动
        channel.enableVibration(true);
        //震动节奏
        channel.setVibrationPattern(new long[]{100, 200});
        return channel;
    }
}
