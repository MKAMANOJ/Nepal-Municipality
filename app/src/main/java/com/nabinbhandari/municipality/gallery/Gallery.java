package com.nabinbhandari.municipality.gallery;

import com.nabinbhandari.ErrorTracker;
import com.nabinbhandari.retrofit.MyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 7:50 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
public class Gallery extends MyResponse {

    private List<GalleryGroup> data;

    public List<GalleryGroup> getGroups() {
        if (data == null) {
            ErrorTracker.track();
            data = new ArrayList<>();
        }
        return data;
    }

}
