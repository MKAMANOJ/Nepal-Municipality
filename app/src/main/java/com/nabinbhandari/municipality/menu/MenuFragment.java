package com.nabinbhandari.municipality.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nabinbhandari.municipality.R;

import java.util.List;

/**
 * Created at 7:40 PM on 1/6/2018.
 *
 * @author bnabin51@gmail.com
 */

public class MenuFragment extends Fragment {

    private List<Category> categories;

    public MenuFragment() {
    }

    public static MenuFragment newInstance(List<Category> categories) {
        MenuFragment fragment = new MenuFragment();
        fragment.categories = categories;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        GridView gridView = view.findViewById(R.id.gridView);
        gridView.setAdapter(new CategoriesAdapter(inflater.getContext(), categories));
        return view;
    }

    private class CategoriesAdapter extends ArrayAdapter<Category> {

        private CategoriesAdapter(@NonNull Context context, @NonNull List<Category> categories) {
            super(context, R.layout.item_category, categories);
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
            nameTextView.setText(category.toString());
            ImageView imageView = view.findViewById(R.id.imagePreview);
            Glide.with(imageView).load(category.resId).into(imageView);
            return view;
        }

    }

}
