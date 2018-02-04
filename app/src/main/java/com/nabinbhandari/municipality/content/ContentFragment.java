package com.nabinbhandari.municipality.content;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.BaseFragment;
import com.nabinbhandari.municipality.R;
import com.nabinbhandari.municipality.menu.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created at 11:02 PM on 1/8/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ContentFragment extends BaseFragment {

    private static final String KEY_CATEGORY_ID = "category_id";

    private int categoryId;
    private ContentAdapter contentAdapter;

    private Query dbRef;
    private ChildEventAdapter listener;

    public ContentFragment() {
    }

    public static ContentFragment newInstance(int categoryId) {
        Bundle args = new Bundle();
        args.putInt(KEY_CATEGORY_ID, categoryId);
        ContentFragment contentFragment = new ContentFragment();
        contentFragment.setArguments(args);
        return contentFragment;
    }

    private void readBundle(Bundle args) {
        if (args != null) {
            categoryId = args.getInt(KEY_CATEGORY_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        readBundle(getArguments());
        Context context = getContext() == null ? inflater.getContext() : getContext();
        ListView listView = new ListView(context);
        listView.setSelector(android.R.color.transparent);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        listView.setPadding(padding / 2, padding, padding / 2, padding);
        listView.setDivider(null);
        contentAdapter = new ContentAdapter(context, new ArrayList<Content>());
        listView.setAdapter(contentAdapter);
        loadContents();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content content = contentAdapter.getItem(position);
                if (content == null) return;
                content.file_category_id = categoryId;
                startActivity(new Intent(parent.getContext(), ContentActivity.class)
                        .putExtra(ContentActivity.EXTRA_CONTENT, content));
            }
        });
        return listView;
    }

    private void loadContents() {
        String slug = Category.findSlugById(categoryId);
        dbRef = FirebaseDatabase.getInstance().getReference(slug);
        listener = new ChildEventAdapter(getContext()) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Content content = Content.from(dataSnapshot);
                if (content == null) return;
                contentAdapter.add(content);
                onLoad(contentAdapter.getCount());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Content content = Content.from(dataSnapshot);
                contentAdapter.remove(content);
                onLoad(contentAdapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Content content = Content.from(dataSnapshot);
                if (content == null) return;
                int index = contentAdapter.getPosition(content);
                if (index < 0) return;
                Content existing = contentAdapter.getItem(index);
                if (existing == null) return;
                existing.set(content);
                contentAdapter.notifyDataSetChanged();
            }
        };
        startLoading(dbRef, listener);
    }

    @Override
    public void onDestroy() {
        if (dbRef != null) dbRef.removeEventListener(listener);
        super.onDestroy();
    }

    private class ContentAdapter extends ArrayAdapter<Content> {

        ArrayList<Content> contents;

        ContentAdapter(@NonNull Context context, ArrayList<Content> contents) {
            super(context, R.layout.item_content, contents);
            this.contents = contents;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            Context context = parent.getContext();
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.item_content, parent, false);
            }
            Content content = getItem(position);
            if (content == null) return view;

            TextView titleTextView = view.findViewById(R.id.content_title_text);
            TextView descTextView = view.findViewById(R.id.content_desc_text);

            titleTextView.setText(content.title);
            descTextView.setText(content.description);

            return view;
        }

        @Override
        public void notifyDataSetChanged() {
            Collections.sort(contents, new Comparator<Content>() {
                @Override
                public int compare(Content c1, Content c2) {
                    return c1.order - c2.order;
                }
            });
            super.notifyDataSetChanged();
        }

    }

}
