package com.nabinbhandari.retrofit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created at 10:10 PM on 12/25/2017.
 *
 * @author bnabin51@gmail.com
 */
public class ImageFragment extends Fragment {

    private static final String KEY_URL = "file_name";
    private static final String DESCRIPTION = "description";

    public ImageFragment() {
    }

    public static ImageFragment newInstance(String fileName, String description) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, fileName);
        args.putString(DESCRIPTION, description);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @SuppressLint("StaticFieldLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.photo_preview, null, false);
        Bundle args = getArguments();
        if (args == null) return view;
        final String url = args.getString(KEY_URL);
        final String description = args.getString(DESCRIPTION);

        final ImageView imagePreview = view.findViewById(R.id.imagePreview);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView descTextView = view.findViewById(R.id.descTextView);
        descTextView.setText(description);
        progressBar.setVisibility(View.GONE);

        Glide.with(imagePreview).setDefaultRequestOptions(Utils.getPlaceholderOptions())
                .load(url).into(imagePreview);

        return view;
    }

}
