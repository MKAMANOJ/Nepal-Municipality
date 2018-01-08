package com.nabinbhandari.municipality.gallery;

import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;
import com.nabinbhandari.ErrorTracker;

import java.io.Serializable;

/**
 * Created at 7:42 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */
@SuppressWarnings("WeakerAccess")
@IgnoreExtraProperties
public class GalleryCategory implements Serializable {

    public int id;
    public String name;

    public String key;

    public String firstPhotoUrl;

    @Nullable
    public static GalleryCategory from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        GalleryCategory galleryCategory = dataSnapshot.getValue(GalleryCategory.class);
        if (galleryCategory != null) {
            galleryCategory.key = dataSnapshot.getKey();
        }
        return galleryCategory;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GalleryCategory && ((GalleryCategory) obj).id == id;
    }

    public void set(GalleryCategory galleryCategory) {
        id = galleryCategory.id;
        name = galleryCategory.name;
        key = galleryCategory.key;
    }

    public String getDescription() {
        return name;
    }

    public String getFirstPhotoUrl() {
        if (firstPhotoUrl == null) {
            ErrorTracker.track();
            return "error.jpg";
        }
        return firstPhotoUrl;
    }

}
