package com.nabinbhandari.municipality;

import android.app.Application;

/**
 * Created at 10:31 PM on 1/15/2018.
 *
 * @author bnabin51@gmail.com
 */

public class AppConstants extends Application {

    public static String BASE_URL;

    @Override
    public void onCreate() {
        super.onCreate();
        BASE_URL = getString(R.string.base_url);
    }

}
