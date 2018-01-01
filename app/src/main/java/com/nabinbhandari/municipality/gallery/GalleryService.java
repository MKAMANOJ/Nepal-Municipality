package com.nabinbhandari.municipality.gallery;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created at 8:12 PM on 1/1/2018.
 *
 * @author bnabin51@gmail.com
 */

public interface GalleryService {

    @GET("gallery-categories")
    Call<Gallery> getGalleryData();

    @GET("gallery/category/{id}/images")
    Call<GalleryGroup> getGroupData(@Path("id") int groupId);

}
