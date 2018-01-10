package com.nabinbhandari.downloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nabinbhandari.Logger;
import com.nabinbhandari.retrofit.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created at 7:00 PM on 1/9/2018
 * <p>
 * A class which can be used for easy file downloading and caching.
 *
 * @author bnabin51@gmail.com
 */

public class FileDownloader {

    private static final String TAG = FileDownloader.class.getSimpleName();

    //private final Context mContext;
    private final File cacheDir;

    /**
     * Creates a file downloader with cache dir in app's internal storage.
     *
     * @param context the Context.
     * @throws IOException if the cache dir does not exist and cannot be created.
     */
    public FileDownloader(@NonNull Context context) throws IOException {
        //mContext = context;
        cacheDir = new File(context.getCacheDir(), ".file_downloader");
        initCacheDir();
    }

    /**
     * Creates a file downloader with cache dir in supplied directory.
     *
     * @param cacheDir the intended cache directory.
     *                 //@param context  the Context.
     * @throws IOException if the cache dir does not exist and cannot be created.
     */
    public FileDownloader(/*@NonNull Context context, */@NonNull File cacheDir) throws IOException {
        //mContext = context;
        this.cacheDir = cacheDir;
        initCacheDir();
        double random = Math.random();
    }

    /**
     * Internal method for initializing the cache directory.
     *
     * @throws IOException if the cache dir does not exist and cannot be created.
     */
    private void initCacheDir() throws IOException {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new IOException("Unable to create cache dir!");
        }
    }

    @NonNull
    public File getCacheDir() throws IOException {
        initCacheDir();
        return cacheDir;
    }

    @NonNull
    private File getCacheFile(@NonNull String url) throws IOException {
        String hash = getMD5Hash(url);
        return new File(getCacheDir(), hash);
    }

    @Nullable
    public Call<ResponseBody> loadFile(@NonNull String url, boolean forceDownload,
                                       @NonNull final LoadCallback<File> callback) {
        File cacheFile;
        try {
            cacheFile = getCacheFile(url);
        } catch (IOException e) {
            callback.onError(e, e.getMessage());
            return null;
        }
        if (forceDownload || !cacheFile.exists()) {
            return downloadFile(url, cacheFile, callback);
        } else {
            callback.onComplete(cacheFile, null, null);
        }
        return null;
    }

    /**
     * A class which holds a reference of any type of object.
     *
     * @param <T> any class type.
     */
    public static class ReferenceHolder<T> {
        public T reference;
    }

    /**
     * WHAT DID I JUST WRITE??
     *
     * @param url      the url of the image.
     * @param options  the optional bitmap loading options.
     * @param callback the callback object.
     * @return the reference holder which contains null if loading from cache,
     * or the retrofit call object if downloading from network.
     */
    @Nullable
    public ReferenceHolder<Call<ResponseBody>> loadBitmap(@NonNull final String url,
                                                          @Nullable final BitmapFactory.Options options,
                                                          @NonNull final LoadCallback<Bitmap> callback) {
        final ReferenceHolder<Call<ResponseBody>> callHolder = new ReferenceHolder<>();
        final ReferenceHolder<LoadCallback<File>> bitmapCallbackHolder = new ReferenceHolder<>();

        bitmapCallbackHolder.reference = new LoadCallback<File>() {
            @Override
            public void onComplete(@Nullable File output, @Nullable Throwable t,
                                   @Nullable String message) {
                if (t != null) {
                    callback.onError(t, message);
                } else if (output != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(output.getAbsolutePath(), options);
                    if (bitmap != null) {
                        callback.onComplete(bitmap, null, null);
                        Logger.log(TAG, "Bitmap obtained for " + url);
                    } else if (callHolder.reference == null) {
                        Logger.log(TAG, "tried from cache but failed. for " + url);
                        // tried file from cache; retry by forcefully downloading.
                        if (bitmapCallbackHolder.reference != null) {
                            // this reference will be null after one failed attempt.
                            Logger.log(TAG, "Retrying " + url);
                            callHolder.reference = loadFile(url, true,
                                    bitmapCallbackHolder.reference);
                            if (callHolder.reference == null) {
                                Logger.log(TAG, "disabling next retry for " + url);
                                // this value will be null only if there is error getting cache file.
                                // so clear the reference of callback holder so that it won't try
                                // downloading again.
                                bitmapCallbackHolder.reference = null;
                            }
                        }
                    } else {
                        callback.onError(new Exception(
                                "Unable to decode bitmap from given source."), message);
                    }
                } else {
                    // should never happen.
                    String error = "Both output and error cannot be null.";
                    callback.onError(new IllegalStateException(error), message);
                }
            }
        };
        callHolder.reference = loadFile(url, false, bitmapCallbackHolder.reference);
        return callHolder;
    }

    /**
     * Download the content from supplied URL to the given file.
     *
     * @param url      the source URL.
     * @param outFile  the destination file.
     * @param callback the callback listener.
     * @return the retrofit Call object for future reference.
     */
    @NonNull
    public Call<ResponseBody> downloadFile(@NonNull String url, @NonNull final File outFile,
                                           @NonNull final LoadCallback<File> callback) {
        Call<ResponseBody> call = RetrofitHelper.getDownloader(url);
        Logger.log(TAG, "downloading from " + url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    final ResponseBody body = response.body();
                    if (body == null) {
                        String errorMessage = "Empty response received from the server!";
                        Logger.log(TAG, errorMessage);
                        callback.onError(new IOException(errorMessage), errorMessage);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Logger.log(TAG, "saving stream to " + outFile.getAbsolutePath());
                                    saveStream(body.byteStream(), outFile);
                                } catch (Throwable t) {
                                    Logger.log(TAG, "save error");
                                    callback.onError(t, t.getMessage());
                                    return;
                                }
                                callback.onComplete(outFile, null, "Download complete.");
                                Logger.log(TAG, "complete");
                            }
                        }).start();
                    }
                } else {
                    callback.onError(new IOException("Failed to download file: " + response.code()),
                            Utils.getErrorMessage(response));
                    Logger.log(TAG, "unsuccessful");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onError(t, "Error occurred while downloading the file." +
                        "\nPlease check your internet connection.");
            }
        });

        return call;
    }

    private void saveStream(InputStream inputStream, File outFile) throws IOException {
        if (outFile.exists() && !outFile.delete()) {
            throw new IOException("File already exists and cannot be deleted!");
        }
        File parent = outFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Unable to create parent directory for destination file!");
        }
        FileOutputStream out = new FileOutputStream(outFile);
        copyStream(inputStream, out);
        out.close();
    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @NonNull
    private String getMD5Hash(@NonNull String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ignored) {
            return input;
        }
    }

}
