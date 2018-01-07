package com.nabinbhandari;

import com.nabinbhandari.retrofit.BuildConfig;

/**
 * Created at 9:37 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

public class ErrorTracker {

    public static void track() {
        if (!BuildConfig.DEBUG) return;
        try {
            throw new Exception("Here");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
