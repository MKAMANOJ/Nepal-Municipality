package com.nabinbhandari.notification;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created at 7:46 PM on 1/20/2018.
 *
 * @author bnabin51@gmail.com
 */

@IgnoreExtraProperties
@SuppressWarnings("WeakerAccess")
public class NotificationContent {

    public int id;
    public String key;
    public String message;
    public String title;
    public String description;
    public String updated_at;

    public static NotificationContent from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        NotificationContent content = dataSnapshot.getValue(NotificationContent.class);
        if (content != null) {
            content.key = dataSnapshot.getKey();
        }
        return content;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NotificationContent && ((NotificationContent) obj).id == id;
    }

    public void set(NotificationContent content) {
        this.id = content.id;
        this.key = content.key;
        this.message = content.message;
        this.title = content.title;
        this.description = content.description;
        this.updated_at = content.updated_at;
    }

}
