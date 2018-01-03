package com.nabinbhandari.municipality.gallery;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created at 7:42 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class PhotoItem implements Serializable {

    class ImageLink implements Serializable {
        public String image, thumbnail;
    }

    public int id;
    private String title;
    private String url, thumbUrl;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") //will update from retrofit.
    private ArrayList<ImageLink> links;

    public String getUrl() {
        if (links == null) return "NULL";
        if (url == null) {
            url = links.get(0).image;
        }
        return url;
    }

    public String getThumbUrl() {
        if (links == null) return "NULL";
        if (thumbUrl == null) {
            thumbUrl = links.get(0).thumbnail;
        }
        return thumbUrl;
    }

    public String getDescription() {
        return title;
    }

}
