package com.nabinbhandari.municipality.impcontacts;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created at 11:06 AM on 1/14/2018.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
@IgnoreExtraProperties
public class ContactCategory implements Serializable {

    public int id;
    public String name;
    public int order;

    public String key;

    public static ContactCategory from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        ContactCategory category = dataSnapshot.getValue(ContactCategory.class);
        if (category != null) {
            category.key = dataSnapshot.getKey();
        }
        return category;
    }

    public void set(ContactCategory category) {
        this.id = category.id;
        this.name = category.name;
        this.order = category.order;
        this.key = category.key;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContactCategory && id == ((ContactCategory) obj).id;
    }

}
