package com.nabinbhandari.downloader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nabinbhandari.municipality.BuildConfig;

/**
 * Created at 7:47 PM on 1/9/2018.
 * <p>
 * File load callback listener.
 *
 * @author bnabin51@gmail.com
 */

public abstract class LoadCallback<T> {

    /**
     * Called when the loading task fails.
     * <p>
     * Method {@link #onComplete} wil eventually be called with non-null {@link Throwable} parameter.
     *
     * @param t       the Throwable which describes the problem regarding failure.
     * @param message information, if any.
     */
    @SuppressWarnings("WeakerAccess")
    public void onError(@NonNull Throwable t, @Nullable String message) {
        if (BuildConfig.DEBUG) {
            t.printStackTrace();
        }
        onComplete(null, t, message);
    }

    /**
     * Called when the loading task completes.
     *
     * @param output  the loaded object.
     * @param t       null if load succeeds, non-null object if failed.
     * @param message information, if any.
     */
    public abstract void onComplete(@Nullable T output, @Nullable Throwable t, @Nullable String message);

}
