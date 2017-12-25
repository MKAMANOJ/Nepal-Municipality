package com.nabinbhandari.retrofit;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

/**
 * @author bnabin51@gmail.com
 */
public interface ImageLoadListener {

    /**
     * Callback method for loading image.
     *
     * @param image        the loaded image if loaded and null if not loaded.
     * @param errorMessage error message.
     */
    void onLoadResult(@Nullable Bitmap image, @Nullable String errorMessage);

}
