package com.nabinbhandari.retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.nabinbhandari.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author bnabin51@gmail.com
 */
@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess"})
public class ImageUtils {

    public static Bitmap errorBitmap;
    public static Bitmap loadingBitmap;
    private static PhotoService photoService;
    private static File cacheDir;

    /**
     * List of files which are currently being downloaded. This list is used to keep track of
     * multiple requests requesting for same file at once.
     */
    private static ArrayList<File> currentlyDownloading = new ArrayList<>();

    public static void init(final Context context, PhotoService photoService) {
        ImageUtils.photoService = photoService;
        cacheDir = new File(context.getFilesDir(), "cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (errorBitmap == null || errorBitmap.isRecycled()) {
                    errorBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.placeholder_warning);
                }
                if (loadingBitmap == null || loadingBitmap.isRecycled()) {
                    loadingBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.placeholder_waiting);
                }
            }
        }).start();
    }

    public static File getCacheDir() {
        return cacheDir;
    }

    public static void setBitmapOnUi(final ImageView target, final Bitmap image) {
        target.post(new Runnable() {
            @Override
            public void run() {
                target.setImageBitmap(image == null || image.isRecycled() ?
                        ImageUtils.errorBitmap : image);
            }
        });
    }

    public static File getCacheFile(String fileName) {
        try {
            return new File(cacheDir, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadImageAsync(@NonNull final ImageView target,
                                      final String fileName, final boolean showErrorToast) {
        final File tempFile = getCacheFile(fileName);
        if (tempFile == null) {
            setBitmapOnUi(target, ImageUtils.errorBitmap);
            return;
        }
        setBitmapOnUi(target, loadingBitmap);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if (tempFile.exists()) {
                    //System.err.println(tempFile.getAbsolutePath() + " exists.");
                    bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                } //else System.err.println(tempFile.getAbsolutePath() + " does not exist.");
                if (bitmap == null) {
                    //System.err.println("bitmap null - downloading");
                    downloadImage(fileName, tempFile,
                            true, new ImageLoadListener() {
                                @Override
                                public void onLoadResult(Bitmap image, String errorMessage) {
                                    setBitmapOnUi(target, image);
                                    if (showErrorToast) {
                                        Utils.showToastOnUI(target, errorMessage);
                                    }
                                }
                            });
                } else {
                    //System.err.println("bitmap not null");
                    setBitmapOnUi(target, bitmap);
                }
            }
        }).start();
    }

    /**
     * @param outFile         if not null, bitmap will first be saved to the file and decoded from it.
     * @param checkConcurrent false if concurrent download of same file need not be checked.
     */
    public static Call<ResponseBody> downloadImage(String fileName, final File outFile, boolean checkConcurrent,
                                                   final ImageLoadListener imageLoadListener) {
        Call<ResponseBody> call = photoService.getPhoto(fileName);
        if (checkConcurrent && outFile != null) {
            if (currentlyDownloading.contains(outFile)) {
                //System.err.println("already downloading " + outFile.getAbsolutePath());
                imageLoadListener.onLoadResult(loadingBitmap, null);
                call.cancel();
                return call;
            } //else System.err.println("download starting " + outFile.getAbsolutePath());
            currentlyDownloading.add(outFile);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    final ResponseBody body = response.body();
                    if (body != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = null;
                                if (outFile == null) {
                                    bitmap = BitmapFactory.decodeStream(body.byteStream());
                                } else if (saveStream(body.byteStream(), outFile)) {
                                    bitmap = BitmapFactory.decodeFile(outFile.getAbsolutePath());
                                }
                                imageLoadListener.onLoadResult(bitmap, bitmap == null ?
                                        "Unable to download photo." : null);
                                currentlyDownloading.remove(outFile);
                            }
                        }).start();
                    } else {
                        currentlyDownloading.remove(outFile);
                        imageLoadListener.onLoadResult(null, "Null body");
                    }
                } else {
                    currentlyDownloading.remove(outFile);
                    imageLoadListener.onLoadResult(null, Utils.getErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.printStackTrace(t);
                currentlyDownloading.remove(outFile);
                imageLoadListener.onLoadResult(null, "Exception: " + t.getMessage());
            }
        });
        return call;
    }

    private static boolean saveStream(InputStream inputStream, File outFile) {
        if (outFile.exists()) {
            outFile.delete();
        }
        File parent = outFile.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(outFile);
            copyStream(inputStream, out);
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
        return true;
    }

    public static void copyStream(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[4096];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
