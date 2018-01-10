package com.nabinbhandari.downloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

@SuppressWarnings("WeakerAccess")
public class FileDownloader {

    private final File cacheDir;

    /**
     * Creates a file downloader with cache dir in app's internal storage.
     *
     * @param context the Context.
     * @throws IOException if the cache dir does not exist and cannot be created.
     */
    public FileDownloader(@NonNull Context context) throws IOException {
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
    @SuppressWarnings("unused")
    public FileDownloader(@NonNull File cacheDir) throws IOException {
        this.cacheDir = cacheDir;
        initCacheDir();
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
    public File getCacheFile(@NonNull String url) throws IOException {
        String hash = getMD5Hash(url);
        return new File(getCacheDir(), hash);
    }

    @Nullable
    public Call<ResponseBody> loadFile(@NonNull String url, @NonNull final LoadCallback<File> callback) {
        File cacheFile;
        try {
            cacheFile = getCacheFile(url);
        } catch (IOException e) {
            callback.onError(e, e.getMessage());
            return null;
        }
        if (!cacheFile.exists()) {
            return downloadFile(url, cacheFile, callback);
        } else {
            callback.onComplete(cacheFile, null, null);
        }
        return null;
    }

    /**
     * This method is first searches the cached image for the url, if not found caches it,
     * then forwards the image to the callback.
     *
     * @param url      the url of the image.
     * @param options  the optional bitmap loading options.
     * @param callback the callback object.
     * @return the reference holder which contains null if loading from cache,
     * or the retrofit call object if downloading from network.
     */
    @Nullable
    public Call<ResponseBody> loadBitmap(@NonNull final String url,
                                         @Nullable final BitmapFactory.Options options,
                                         @NonNull final LoadCallback<Bitmap> callback) {
        LoadCallback<File> downloadCallback = new LoadCallback<File>() {
            @Override
            public void onComplete(@Nullable File output, @Nullable Throwable t,
                                   @Nullable String message) {
                if (t != null) {
                    callback.onError(t, message);
                } else if (output != null) {
                    Bitmap bitmap;
                    try {
                        bitmap = BitmapFactory.decodeFile(output.getAbsolutePath(), options);
                    } catch (Throwable thr) {
                        callback.onError(thr, "Unable to decode bitmap!");
                        return;
                    }
                    if (bitmap != null) {
                        callback.onComplete(bitmap, null, null);
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
        return loadFile(url, downloadCallback);
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
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    final ResponseBody body = response.body();
                    if (body == null) {
                        String errorMessage = "Empty response received from the server!";
                        callback.onError(new IOException(errorMessage), errorMessage);
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    saveStream(body.byteStream(), outFile);
                                } catch (Throwable t) {
                                    callback.onError(t, t.getMessage());
                                    return;
                                }
                                callback.onComplete(outFile, null, "Download complete.");
                            }
                        }).start();
                    }
                } else {
                    callback.onError(new IOException("Failed to download file: " + response.code()),
                            Utils.getErrorMessage(response));
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

    public static void saveStream(InputStream inputStream, File outFile) throws IOException {
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

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    @NonNull
    private String getMD5Hash(@NonNull String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
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
