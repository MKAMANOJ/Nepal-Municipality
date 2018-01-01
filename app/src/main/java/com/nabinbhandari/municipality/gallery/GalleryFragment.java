package com.nabinbhandari.municipality.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.retrofit.RetrofitUtils;
import com.nabinbhandari.retrofit.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nabinbhandari.retrofit.PreviewActivity.getRequestOptions;

/**
 * Created at 7:15 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

public class GalleryFragment extends Fragment {

    private Call<Gallery> call;
    private static List<Call<GalleryGroup>> calls = new ArrayList<>();

    private GalleryGroupAdapter adapter;

    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) call.cancel();
        for (Call call : calls) {
            call.cancel();
        }
        calls.clear();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Context context = getContext() == null ? inflater.getContext() : getContext();
        FrameLayout layout = new FrameLayout(context);
        final GridView gridView = new GridView(context);
        final ProgressBar progressBar = new ProgressBar(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(params);
        layout.addView(gridView);
        layout.addView(progressBar);
        gridView.setBackgroundColor(Color.BLACK);
        gridView.setNumColumns(2);

        call = RetrofitUtils.getRetrofit().create(GalleryService.class).getGalleryData();
        call.enqueue(new Callback<Gallery>() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(Call<Gallery> call, Response<Gallery> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    adapter = new GalleryGroupAdapter(context, R.layout.item_gallery,
                            response.body().getGroups());
                    loadGroups(response.body().getGroups(), adapter);
                    gridView.setAdapter(adapter);
                } else {
                    String error = Utils.getErrorMessage(response);
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
                    System.err.println(error);
                }
            }

            @Override
            public void onFailure(Call<Gallery> call, Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return layout;
    }

    private static void loadGroups(final List<GalleryGroup> galleryGroups,
                                   final GalleryGroupAdapter adapter) {
        for (final GalleryGroup group : galleryGroups) {
            if (group.isLoaded) continue;
            final Call<GalleryGroup> call = RetrofitUtils.getRetrofit().create(GalleryService.class)
                    .getGroupData(group.id);
            calls.add(call);
            call.enqueue(new Callback<GalleryGroup>() {
                @Override
                public void onResponse(Call<GalleryGroup> call, Response<GalleryGroup> response) {
                    if (response.isSuccessful()) {
                        GalleryGroup detailedGroup = response.body();
                        if (detailedGroup == null || detailedGroup.getPhotos() == null) {
                            group.loadError = true;
                            return;
                        }
                        group.setPhotos(detailedGroup.getPhotos());
                        group.isLoaded = true;
                        if (adapter != null) adapter.notifyDataSetChanged();
                    } else {
                        System.err.println(Utils.getErrorMessage(response));
                    }
                }

                @Override
                public void onFailure(Call<GalleryGroup> call, Throwable t) {
                    group.loadError = true;
                    t.printStackTrace();
                    if (adapter != null) adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private static class GalleryGroupAdapter extends ArrayAdapter<GalleryGroup> {

        private GalleryGroupAdapter(@NonNull Context context, @LayoutRes int resource,
                                    @NonNull List<GalleryGroup> objects) {
            super(context, resource, objects);
            System.err.println("groups: " + objects.size());
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final GalleryGroup group = getItem(position);
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

            //ImageUtils.loadImageAsync(imageView, group.getFirstPhotoName(), false);

            if (group.isLoaded) {
                Glide.with(imageView).setDefaultRequestOptions(getRequestOptions())
                        .load(group.getFirstPhotoUrl()).into(imageView);
            } else {
                Glide.with(imageView).load(group.loadError ?
                        R.drawable.placeholder_warning : R.drawable.placeholder_waiting).into(imageView);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (group.isLoaded) {
                        Intent intent = new Intent(v.getContext(), GalleryGroupActivity.class)
                                .putExtra(GalleryGroupActivity.EXTRA_GALLERY_GROUP, group);
                        v.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(v.getContext(), "Not loaded!", Toast.LENGTH_SHORT).show();
                        if (group.loadError) {
                            group.loadError = false;
                            loadGroups(Collections.singletonList(group), GalleryGroupAdapter.this);
                            Glide.with(imageView).setDefaultRequestOptions(getRequestOptions())
                                    .load(R.drawable.placeholder_waiting).into(imageView);
                        }
                    }
                }
            });
            return convertView;
        }

    }

}
