package com.nabinbhandari.retrofit;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created at 10:10 PM on 12/25/2017.
 *
 * @author bnabin51@gmail.com
 */
public class ImageFragment extends Fragment {

    private static final String KEY_FILENAME = "file_name";
    private static final String DESCRIPTION = "description";

    private Call<ResponseBody> call;

    public ImageFragment() {
    }

    public static ImageFragment newInstance(String fileName, String description) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(KEY_FILENAME, fileName);
        args.putString(DESCRIPTION, description);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater().inflate(R.layout.photo_preview, null, false);
        Bundle args = getArguments();
        final String fileName = args.getString(KEY_FILENAME);
        final String description = args.getString(DESCRIPTION);

        final ImageView imagePreview = view.findViewById(R.id.imagePreview);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);

        final File mainFile = ImageUtils.getCacheFile(fileName);
        final File thumbFile = ImageUtils.getCacheFile("thumb_" + fileName);

        new AsyncTask<Void, Void, Bitmap>() {
            private boolean mainFound = false;

            protected void onPreExecute() {
                imagePreview.setImageBitmap(ImageUtils.loadingBitmap);
            }

            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                if (mainFile != null && mainFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(mainFile.getAbsolutePath());
                    mainFound = bitmap != null;
                }
                if (bitmap == null && thumbFile != null && thumbFile.exists()) {
                    bitmap = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
                }
                return bitmap;
            }

            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    imagePreview.setImageBitmap(bitmap);
                }
                if (mainFound) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                call = ImageUtils.downloadImage(fileName, mainFile, false, new ImageLoadListener() {
                    @Override
                    public void onLoadResult(@Nullable Bitmap image, @Nullable String errorMessage) {
                        ImageUtils.setBitmapOnUi(imagePreview, image);
                        Utils.showToastOnUI(imagePreview, errorMessage);
                        progressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(ProgressBar.GONE);
                            }
                        });
                    }
                });
            }
        }.execute();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
    }

}
