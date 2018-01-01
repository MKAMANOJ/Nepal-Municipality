package com.nabinbhandari.municipality.gallery;

import com.nabinbhandari.ErrorTracker;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created at 7:42 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class GalleryGroup implements Serializable {

    public boolean isLoaded = false;
    public boolean loadError = false;

    public int id;
    public String name;

    //private String group_desc_en;
    //private String group_desc_np;
    private ArrayList<PhotoItem> data;

    public String getDescription() {
        String sizeString = " (" + getPhotos().size() + ")";
        return name + sizeString;
    }

    public String getFirstPhotoUrl() {
        if (getPhotos().size() == 0) {
            ErrorTracker.track();
            return "error.jpg";
        }
        return data.get(0).getThumbUrl();
    }

    public void setPhotos(ArrayList<PhotoItem> data) {
        this.data = data;
    }

    public ArrayList<PhotoItem> getPhotos() {
        if (data == null) {
            ErrorTracker.track();
            data = new ArrayList<>();
        }
        return data;
    }

}
