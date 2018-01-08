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

@SuppressWarnings({"WeakerAccess", "unused"})
@IgnoreExtraProperties
public class PhotoItem implements Serializable {

    private static final String BASE_URL = "http://manoj.engineeringinnepal.com/palika/storage/";

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

}
