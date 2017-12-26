package com.nabinbhandari.municipality.gallery;

import com.nabinbhandari.ErrorTracker;

import java.io.Serializable;

/**
 * Created at 7:42 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class PhotoItem implements Serializable {

    private String desc_en;
    private String desc_np;
    private String file_name;

    public PhotoItem() {
    }

    public String getDescription() {
        return desc_en;
    }

    public String getFileName() {
        if (file_name == null) {
            ErrorTracker.track();
            file_name = "error.jpg";
        }
        return file_name;
    }

    public String getThumbFileName() {
        return "thumb_" + getFileName();
    }

}
