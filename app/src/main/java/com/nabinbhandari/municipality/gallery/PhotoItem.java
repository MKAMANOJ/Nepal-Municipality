package com.nabinbhandari.municipality.gallery;

import com.nabinbhandari.ErrorTracker;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created at 7:42 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings({"WeakerAccess", "unused", "DeprecatedIsStillUsed"})
public class PhotoItem implements Serializable {

    class ImageLink implements Serializable {
        public String image, thumbnail;
    }

    public int id;
    private String title;

    private ArrayList<ImageLink> links;

    public String getUrl() {
        if (url == null) {
            url = links.get(0).image;
        }
        return url;
    }

    public String getThumbUrl() {
        if (thumbUrl == null) {
            thumbUrl = links.get(0).thumbnail;
        }
        return thumbUrl;
    }

    // private String desc_np;
    // private String file_name;

    private String url, thumbUrl;

    public String getDescription() {
        return title;
    }

    @Deprecated
    public String getFileName() {
        if (url == null) {
            ErrorTracker.track();
            return "error.jpg";
        }
        return getMD5(url);
    }

    @Deprecated
    public String getThumbFileName() {
        if (thumbUrl == null) {
            ErrorTracker.track();
            return "error.jpg";
        }
        return getMD5(thumbUrl);
    }

    public static String getMD5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return new String(digest.digest(input.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
