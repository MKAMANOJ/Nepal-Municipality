package com.nabinbhandari.downloader;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created at 8:07 PM on 1/9/2018.
 *
 * @author bnabin51@gmail.com
 */

class RetrofitHelper {

    static Call<ResponseBody> getDownloader(String url) {
        String baseUrl, name;
        if (url.endsWith("/")) {
            baseUrl = url;
            name = "";
        } else {
            int lastSlashIndex = url.lastIndexOf("/");
            baseUrl = url.substring(0, lastSlashIndex + 1);
            name = url.substring(lastSlashIndex + 1);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .build();
        Downloader downloader = retrofit.create(Downloader.class);
        return downloader.download(name);
    }

    private interface Downloader {
        @GET()
        Call<ResponseBody> download(@Url String name);
    }

}
