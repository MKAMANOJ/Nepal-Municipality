package com.nabinbhandari.firebaseutils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nabinbhandari.municipality.Splash;

import java.util.Map;

/**
 * Created on 7/21/17.
 *
 * @author bnabin51@gmail.com
 */
public class FCMService extends FirebaseMessagingService {

    public static final String KEY_MESSAGE = "message";

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
        Intent intent = null;
        int notificationId = (int) (Math.random() * 1000);

        if (data.containsKey(KEY_MESSAGE)) {
            String jsonString = data.get(KEY_MESSAGE);
            System.err.println("Message: " + jsonString);
            if (jsonString != null) {
                intent = new Intent(this, Splash.class);
                intent.putExtra(Splash.KEY_MESSAGE, jsonString);
                notificationId = 1000 + (int) (1000 * Math.random());
            }
        }

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (intent != null && notification != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                    .setContentTitle(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentIntent(pendingIntent);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.notify(notificationId, builder.build());
            }
        }
    }

}
