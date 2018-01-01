package com.nabinbhandari.municipality.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.retrofit.Image;
import com.nabinbhandari.retrofit.PreviewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 9:50 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

public class GalleryGroupActivity extends AppCompatActivity {

    public static final String EXTRA_GALLERY_GROUP = "gallery_group";
    GalleryGroup galleryGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            GridView gridView = new GridView(this);
            gridView.setBackgroundColor(Color.BLACK);
            gridView.setNumColumns(2);
            setContentView(gridView);
            galleryGroup = (GalleryGroup) getIntent().getSerializableExtra(EXTRA_GALLERY_GROUP);
            gridView.setAdapter(new GalleryGroupAdapter(this, R.layout.item_gallery,
                    galleryGroup.getPhotos()));
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                String title = galleryGroup.getDescription();
                if (title != null) actionBar.setTitle(title);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class GalleryGroupAdapter extends ArrayAdapter<PhotoItem> {

        private final List<PhotoItem> photos;

        private GalleryGroupAdapter(@NonNull Context context, @LayoutRes int resource,
                                    @NonNull List<PhotoItem> photos) {
            super(context, resource, photos);
            this.photos = photos;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            final PhotoItem photoItem = getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_gallery, parent, false);
                final View rootView = convertView;
                convertView.post(new Runnable() {
                    @Override
                    public void run() {
                        rootView.setLayoutParams(new AbsListView.LayoutParams(
                                rootView.getWidth(), rootView.getWidth()));
                    }
                });
            }
            if (photoItem == null) return convertView;
            TextView descTextView = convertView.findViewById(R.id.descTextView);
            descTextView.setText(photoItem.getDescription());
            ImageView imageView = convertView.findViewById(R.id.imagePreview);
            Glide.with(imageView).load(photoItem.getThumbUrl()).into(imageView);
            //ImageUtils.loadImageAsync(imageView, photoItem.getThumbFileName(), false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Image> images = new ArrayList<>();
                    for (PhotoItem photoItem : photos) {
                        images.add(new Image(photoItem.getUrl(), photoItem.getDescription()));
                    }
                    Intent intent = new Intent(v.getContext(), PreviewActivity.class)
                            .putExtra(PreviewActivity.EXTRA_IMAGES, images)
                            .putExtra(PreviewActivity.EXTRA_INDEX, position);
                    v.getContext().startActivity(intent);
                }
            });
            return convertView;
        }

    }

}
