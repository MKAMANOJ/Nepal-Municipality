package com.nabinbhandari.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author bnabin51@gmail.com
 */
public interface PhotoService {

    @GET("images/{filename}")
    Call<ResponseBody> getPhoto(@Path("filename") String filename);

}
