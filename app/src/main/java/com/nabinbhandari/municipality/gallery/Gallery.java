package com.nabinbhandari.municipality.gallery;

import com.nabinbhandari.ErrorTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 7:50 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class Gallery {

    public int status;
    public String message;
    private List<GalleryGroup> groups;

    public List<GalleryGroup> getGroups() {
        if (groups == null) {
            ErrorTracker.track();
            groups = new ArrayList<>();
        }
        return groups;
    }

}
