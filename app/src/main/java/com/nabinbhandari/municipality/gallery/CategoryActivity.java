package com.nabinbhandari.municipality.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.retrofit.Image;
import com.nabinbhandari.retrofit.PreviewActivity;
import com.nabinbhandari.retrofit.Utils;

import java.util.ArrayList;

/**
 * Created at 9:50 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

public class CategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "gallery_group_id";
    public static final String EXTRA_CATEGORY_TITLE = "group_title";

    int categoryId;
    private PhotoItemAdapter adapter;

    private Query reference;
    private ChildEventAdapter listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            GridView gridView = new GridView(this);
            gridView.setBackgroundColor(Color.BLACK);
            gridView.setNumColumns(2);
            setContentView(gridView);
            categoryId = getIntent().getIntExtra(EXTRA_CATEGORY_ID, 0);
            if (categoryId == 0) {
                finish();
                return;
            }

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                if (getIntent().hasExtra(EXTRA_CATEGORY_TITLE)) {
                    String title = getIntent().getStringExtra(EXTRA_CATEGORY_TITLE);
                    if (title != null) actionBar.setTitle(title);
                }
            }

            adapter = new PhotoItemAdapter(this);
            gridView.setAdapter(adapter);

            loadPhotos();

        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadPhotos() {
        reference = FirebaseDatabase.getInstance().getReference("tbl_gallery_images")
                .orderByChild("gallery_category_id")
                .equalTo(categoryId);

        listener = new ChildEventAdapter(this) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PhotoItem item = PhotoItem.from(dataSnapshot);
                if (item == null) return;
                adapter.add(item);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                PhotoItem item = PhotoItem.from(dataSnapshot);
                if (item == null) return;
                adapter.remove(item);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                PhotoItem item = PhotoItem.from(dataSnapshot);
                if (item == null) return;
                int index = adapter.getPosition(item);
                if (index < 0) return;
                PhotoItem current = adapter.getItem(index);
                if (current == null) return;
                current.set(item);
                adapter.notifyDataSetChanged();
            }
        };
        reference.addChildEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        reference.removeEventListener(listener);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class PhotoItemAdapter extends ArrayAdapter<PhotoItem> {

        private PhotoItemAdapter(@NonNull Context context) {
            super(context, R.layout.item_gallery, new ArrayList<PhotoItem>());
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
            Glide.with(imageView).setDefaultRequestOptions(Utils.getPlaceholderOptions())
                    .load(photoItem.getUrl()).into(imageView);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Image> images = new ArrayList<>();
                    for (int i = 0; i < getCount(); i++) {
                        PhotoItem photoItem = getItem(i);
                        if (photoItem == null) continue;
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
