package com.nabinbhandari.firebaseutils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.municipality.Splash;

import java.util.Map;

/**
 * Created on 7/21/17.
 *
 * @author bnabin51@gmail.com
 */
public class FCMService extends FirebaseMessagingService {

    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_SUB_TEXT = "sub_text";

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Default Notification Channel");
            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        System.err.println("Data: " + data.toString());

        String title = "New Notification", text = null, subText = null;
        if (data.containsKey(KEY_TITLE)) title = data.get(KEY_TITLE);
        if (data.containsKey(KEY_TEXT)) text = data.get(KEY_TEXT);
        if (data.containsKey(KEY_SUB_TEXT)) subText = data.get(KEY_SUB_TEXT);

        Intent intent = new Intent(this, Splash.class);
        intent.putExtra(Splash.KEY_MESSAGE, data.toString());
        int notificationId = 1000 + (int) (1000 * Math.random());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setOngoing(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(text)) builder.setContentText(text);
        if (!TextUtils.isEmpty(subText)) builder.setSubText(subText);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }

    }

}
