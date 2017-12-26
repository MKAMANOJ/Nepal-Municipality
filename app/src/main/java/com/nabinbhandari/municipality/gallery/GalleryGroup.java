package com.nabinbhandari.municipality.gallery;

import com.nabinbhandari.ErrorTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 7:42 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GalleryGroup {

    private String group_desc_en;
    private String group_desc_np;
    private List<PhotoItem> photos;

    public String getDescription() {
        String sizeString = " (" + getPhotos().size() + ")";
        return group_desc_en + sizeString;
    }

    public String getFirstPhotoName() {
        if (getPhotos().size() == 0) {
            ErrorTracker.track();
            return "error.jpg";
        }
        return photos.get(0).getFileName();
    }

    public List<PhotoItem> getPhotos() {
        if (photos == null) {
            ErrorTracker.track();
            photos = new ArrayList<>();
        }
        return photos;
    }

}
