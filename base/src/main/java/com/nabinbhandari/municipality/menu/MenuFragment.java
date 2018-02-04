package com.nabinbhandari.municipality.menu;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nabinbhandari.firebaseutils.RemoteConfig;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 7:40 PM on 1/6/2018.
 *
 * @author bnabin51@gmail.com
 */

public class MenuFragment extends Fragment {

    private static final String KEY_CATEGORIES = "categories";

    public interface OnCategoryClickListener {
        void onCategoryClick(@NonNull Category category);
    }

    private OnCategoryClickListener onCategoryClickListener;

    private ArrayList<Category> categories = new ArrayList<>();

    public MenuFragment() {
    }

    public static MenuFragment newInstance(@NonNull ArrayList<Category> categories) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_CATEGORIES, categories);

        MenuFragment fragment = new MenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onCategoryClickListener = (OnCategoryClickListener) context;
        } catch (ClassCastException e) {
            String errorMessage = context.getClass().getName() + " must implement OnCategoryClickListener!";
            throw new RuntimeException(new ClassCastException(errorMessage));
        }
    }

    private void readBundle(Bundle args) {
        if (args != null) {
            //noinspection unchecked
            categories = (ArrayList<Category>) args.getSerializable(KEY_CATEGORIES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        readBundle(getArguments());
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        GridView gridView = view.findViewById(R.id.gridView);
        gridView.setAdapter(new CategoriesAdapter(inflater.getContext(), categories));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = categories.get(position);
                if (category == null) return;
                onCategoryClickListener.onCategoryClick(category);
            }
        });
        return view;
    }

    private static class CategoriesAdapter extends ArrayAdapter<Category> {

        private boolean dynamicColor;

        private CategoriesAdapter(@NonNull Context context, @NonNull List<Category> categories) {
            super(context, R.layout.item_category, categories);
            dynamicColor = context.getResources().getBoolean(R.bool.dynamic_color);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            Context context = parent.getContext();
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.item_category, parent, false);
            }
            Category category = getItem(position);
            if (category == null) return view;
            TextView nameTextView = view.findViewById(R.id.nameTextView);
            nameTextView.setTextColor(RemoteConfig.getMenuTextColor());
            nameTextView.setText(category.toString());
            ImageView imageView = view.findViewById(R.id.imagePreview);

            Glide.with(imageView).load(category.getMenuIconColor(dynamicColor)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> t,
                                               DataSource dataSource, boolean isFirstResource) {
                    if (dynamicColor && resource != null) {
                        resource.setColorFilter(RemoteConfig.getMenuIconColor(), PorterDuff.Mode.SRC_IN);
                    }
                    return false;
                }
            }).into(imageView);
            return view;
        }

    }

}
