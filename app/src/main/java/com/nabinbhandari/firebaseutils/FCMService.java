package com.nabinbhandari.firebaseutils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.nabinbhandari.municipality.AppUtils;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.municipality.content.Content;
import com.nabinbhandari.municipality.content.ContentActivity;
import com.nabinbhandari.municipality.menu.Category;
import com.nabinbhandari.notification.NotificationActivity;

import java.util.List;
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
    private static final String KEY_CONTENT = "content";
    private static final String KEY_KEY = "key";
    private static final String KEY_CATEGORY_ID = "file_category_id";

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
        AppUtils.log("Data: " + data.toString());

        String title = "New Notification", text = "Click to open.", subText = null, key = null;
        if (data.containsKey(KEY_TITLE)) title = data.get(KEY_TITLE);
        if (data.containsKey(KEY_TEXT)) text = data.get(KEY_TEXT);
        if (data.containsKey(KEY_KEY)) key = data.get(KEY_KEY);
        if (data.containsKey(KEY_SUB_TEXT)) subText = data.get(KEY_SUB_TEXT);

        Intent intent = null;
        int categoryId = 0, notificationId = 1000;

        try {
            if (data.containsKey(KEY_CATEGORY_ID)) {
                categoryId = Integer.parseInt(data.get(KEY_CATEGORY_ID));
                notificationId = (int) (1000 + Math.random() * 1000);
                if (categoryId == 15) {
                    String reference = "tbl_push_notifications/" + key + "/message";
                    prepareData(reference);
                    intent = new Intent(this, NotificationActivity.class)
                            .putExtra(NotificationActivity.KEY_DB_LOCATION, reference)
                            .putExtra(NotificationActivity.KEY_TITLE, title);
                } else if (categoryId > 0 && categoryId < 10) {
                    Content content = new Gson().fromJson(data.get(KEY_CONTENT), Content.class);
                    content.key = key;
                    prepareData("tbl_uploaded_files/" + key + "/content");
                    intent = new Intent(this, ContentActivity.class)
                            .putExtra(ContentActivity.EXTRA_CONTENT, content);
                }
            }
            if (intent == null || key == null) throw new Exception();
        } catch (Exception e) {
            AppUtils.printStackTrace(e);
            return;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_notification)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setColor(getColor(R.color.colorPrimary))
                .setOngoing(false)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), getIconForCategory(categoryId)))
                .setContentIntent(pendingIntent);

        if (!TextUtils.isEmpty(text)) builder.setContentText(text);
        if (!TextUtils.isEmpty(subText)) builder.setSubText(subText);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }

    private int getIconForCategory(int categoryId) {
        if (categoryId < 1 || categoryId > 9) {
            return R.mipmap.ic_launcher;
        } else {
            List<Category> categories = Category.getDummyList();
            for (Category category : categories) {
                if (categoryId == category.id) {
                    return category.resId;
                }
            }
        }
        return R.mipmap.ic_launcher;
    }

    private void prepareData(String reference) {
        FirebaseDatabase.getInstance().getReference(reference)
                .addListenerForSingleValueEvent(new ValueEventAdapter(null) {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    }
                });
    }

}
