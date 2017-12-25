package com.nabinbhandari.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author bnabin51@gmail.com
 */
public class RetrofitUtils {

    private static Retrofit retrofit;

    private static final String BASE_URL = "http://192.168.137.1/palika/";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
