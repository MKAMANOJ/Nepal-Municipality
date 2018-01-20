package com.nabinbhandari.imageviewer;

import java.io.Serializable;

/**
 * @author Nabin Bhandari
 */

public class Image implements Serializable {

    final String url;
    final String description;

    public Image(String url, String description) {
        this.url = url;
        this.description = description;
    }

}
