package com.nabinbhandari;

import android.support.annotation.Nullable;
import android.util.Log;

import com.nabinbhandari.retrofit.BuildConfig;

/**
 * Helper class for logging and showing toast.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("unused")
public class Logger {

    private static final boolean LOG_DISABLED = BuildConfig.DEBUG;

    /**
     * Helper method for logging.
     *
     * @param tag     tag for log if not null, if null, log will not be shown.
     * @param message message to be logged if not null.
     */
    public static void log(@Nullable Object tag, String message) {
        if (LOG_DISABLED || tag == null || message == null) return;
        Log.e(tag instanceof CharSequence ? tag.toString() : tag.getClass().getSimpleName(), message);
    }

    public static void printStackTrace(Throwable t) {
        if (LOG_DISABLED || t == null) return;
        t.printStackTrace();
    }

}
