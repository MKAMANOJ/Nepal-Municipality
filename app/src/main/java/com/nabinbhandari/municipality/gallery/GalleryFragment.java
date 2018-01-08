package com.nabinbhandari.municipality.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;

import static com.nabinbhandari.retrofit.PreviewActivity.getRequestOptions;

/**
 * Created at 7:15 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

public class GalleryFragment extends Fragment {

    private GalleryGroupAdapter adapter;

    private DatabaseReference dbReference;
    private ChildEventListener categoriesListener;

    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbReference.removeEventListener(categoriesListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Context context = getContext() == null ? inflater.getContext() : getContext();
        final GridView gridView = new GridView(context);
        gridView.setBackgroundColor(Color.BLACK);
        gridView.setNumColumns(2);

        adapter = new GalleryGroupAdapter(context);
        gridView.setAdapter(adapter);

        loadCategories();

        return gridView;
    }

    private void loadCategories() {
        dbReference = FirebaseDatabase.getInstance().getReference("tbl_gallery_categories");
        categoriesListener = new ChildEventAdapter(getContext()) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final GalleryCategory group = GalleryCategory.from(dataSnapshot);
                if (group == null) return;
                loadGroupPhotoUrl(group, new Runnable() {
                    @Override
                    public void run() {
                        adapter.add(group);
                    }
                });
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final GalleryCategory group = GalleryCategory.from(dataSnapshot);
                if (group == null) return;
                adapter.remove(group);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final GalleryCategory group = GalleryCategory.from(dataSnapshot);
                if (group == null) return;
                int position = adapter.getPosition(group);
                if (position < 0) return;
                GalleryCategory existing = adapter.getItem(position);
                if (existing == null) return;
                existing.set(group);
                loadGroupPhotoUrl(existing, new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        };
        dbReference.addChildEventListener(categoriesListener);
    }

    private void loadGroupPhotoUrl(final GalleryCategory group, final Runnable runnable) {
        System.err.println("processing: " + group.name);

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tbl_gallery_images");

        // this may cause memory leak if there are no items for the group.
        final ChildEventAdapter listener = new ChildEventAdapter(getContext()) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ref.removeEventListener(this);
                PhotoItem photoItem = PhotoItem.from(dataSnapshot);
                if (photoItem == null) return;
                group.firstPhotoUrl = photoItem.getUrl();
                if (group.firstPhotoUrl == null) return;
                if (runnable != null) {
                    runnable.run();
                }
            }
        };

        ref.orderByChild("gallery_category_id")
                .equalTo(group.id)
                .limitToFirst(1)
                .addChildEventListener(listener);
    }

    private static class GalleryGroupAdapter extends ArrayAdapter<GalleryCategory> {

        private GalleryGroupAdapter(@NonNull Context context) {
            super(context, R.layout.item_gallery, new ArrayList<GalleryCategory>());
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final GalleryCategory group = getItem(position);
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
            if (group == null) return convertView;
            TextView descTextView = convertView.findViewById(R.id.descTextView);
            descTextView.setText(group.getDescription());

            final ImageView imageView = convertView.findViewById(R.id.imagePreview);
            Glide.with(imageView).setDefaultRequestOptions(getRequestOptions())
                    .load(group.getFirstPhotoUrl()).into(imageView);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CategoryActivity.class)
                            .putExtra(CategoryActivity.EXTRA_CATEGORY_ID, group.id)
                            .putExtra(CategoryActivity.EXTRA_CATEGORY_TITLE, group.name);
                    v.getContext().startActivity(intent);
                }
            });
            return convertView;
        }

    }

}
