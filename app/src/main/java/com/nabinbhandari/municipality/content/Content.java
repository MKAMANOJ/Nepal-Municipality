package com.nabinbhandari.municipality.content;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created at 10:48 PM on 1/8/2018.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
@IgnoreExtraProperties
public class Content implements Serializable {

    private static final String BASE_URL = "http://manoj.engineeringinnepal.com/palika/storage/";

    public String content_type;
    public String created_at;
    public String description;
    public int file_category_id;
    public String file_name;
    public int id;
    public int order;
    public String original_filename;
    public String title;
    public String updated_at;

    public String key;

    public void set(Content content) {
        key = content.key;
        content_type = content.content_type;
        created_at = content.created_at;
        description = content.description;
        file_category_id = content.file_category_id;
        file_name = content.file_name;
        id = content.id;
        order = content.order;
        original_filename = content.original_filename;
        title = content.title;
        updated_at = content.updated_at;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Content && ((Content) obj).id == id;
    }

    public static Content from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        Content content = dataSnapshot.getValue(Content.class);
        if (content != null) {
            content.key = dataSnapshot.getKey();
        }
        return content;
    }

    public String getUrl() {
        if (file_name == null) return null;
        return BASE_URL + file_name;
    }

}
