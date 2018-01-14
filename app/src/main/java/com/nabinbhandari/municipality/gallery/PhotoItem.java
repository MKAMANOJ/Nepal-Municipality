package com.nabinbhandari.municipality.gallery;

import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created at 7:42 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
@IgnoreExtraProperties
public class PhotoItem implements Serializable {

    private static final String BASE_URL = "http://palika.engineeringinnepal.com/palika/storage/";

    public int id;
    public int gallery_category_id;

    public String name; // relative url
    public String title;
    public String description;
    public String original_filename;

    public String key;

    public String getUrl() {
        if (name == null) return null;
        return BASE_URL + name;
    }

    @Nullable
    public static PhotoItem from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        PhotoItem photoItem = dataSnapshot.getValue(PhotoItem.class);
        if (photoItem != null) {
            photoItem.key = dataSnapshot.getKey();
        }
        return photoItem;
    }

    public String getDescription() {
        return description;
    }

    public void set(PhotoItem item) {
        id = item.id;
        gallery_category_id = item.gallery_category_id;
        name = item.name;
        title = item.title;
        description = item.description;
        original_filename = item.original_filename;
        key = item.key;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PhotoItem && ((PhotoItem) obj).id == id;
    }

}
