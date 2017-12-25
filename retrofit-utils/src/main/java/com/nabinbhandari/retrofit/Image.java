package com.nabinbhandari.retrofit;

import java.io.Serializable;

/**
 * @author Nabin Bhandari
 */

public class Image implements Serializable {

    final String fileName;
    final String description;

    public Image(String fileName, String description) {
        this.fileName = fileName;
        this.description = description;
    }

}
