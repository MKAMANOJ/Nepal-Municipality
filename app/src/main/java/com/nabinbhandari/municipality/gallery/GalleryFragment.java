package com.nabinbhandari.municipality.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nabinbhandari.AssetReader;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.retrofit.Image;
import com.nabinbhandari.retrofit.ImageUtils;
import com.nabinbhandari.retrofit.PreviewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 7:15 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

public class GalleryFragment extends Fragment {

    private Gallery gallery;

    public static String getSampleJSON(Context context) {
        return AssetReader.readStringAsset(context, "gallery_sample.json", null);
    }

    public GalleryFragment() {
    }

    public static GalleryFragment newInstance(String galleryJSON) {
        GalleryFragment galleryFragment = new GalleryFragment();
        Gson gson = new Gson();
        galleryFragment.gallery = gson.fromJson(galleryJSON, Gallery.class);
        return galleryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        GridView gridView = new GridView(getContext());
        gridView.setBackgroundColor(Color.BLACK);
        gridView.setNumColumns(2);
        gridView.setAdapter(new GalleryGroupAdapter(getContext(), R.layout.gallery_group,
                gallery.getGroups()));
        return gridView;
    }

    private static class GalleryGroupAdapter extends ArrayAdapter<GalleryGroup> {

        private GalleryGroupAdapter(@NonNull Context context, @LayoutRes int resource,
                                    @NonNull List<GalleryGroup> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final GalleryGroup group = getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.gallery_group, parent, false);
                final View rootView = convertView;
                convertView.post(new Runnable() {
                    @Override
                    public void run() {
                        rootView.setLayoutParams(new AbsListView.LayoutParams(
                                rootView.getWidth(), rootView.getWidth()));
                    }
                });
            }
            if (group == null) return convertView;
            TextView group_desc = convertView.findViewById(R.id.groupNameTextView);
            group_desc.setText(group.getDescription());
            ImageView imageView = convertView.findViewById(R.id.imagePreview);
            ImageUtils.loadImageAsync(imageView, group.getFirstPhotoName(), false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Image> images = new ArrayList<>();
                    for (PhotoItem photoItem : group.getPhotos()) {
                        images.add(new Image(photoItem.getFileName(), photoItem.getDescription()));
                    }
                    Intent intent = new Intent(v.getContext(), PreviewActivity.class)
                            .putExtra(PreviewActivity.EXTRA_IMAGES, images);
                    v.getContext().startActivity(intent);
                }
            });
            return convertView;
        }

    }

}
