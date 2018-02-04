package com.nabinbhandari.municipality.impcontacts;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.BaseFragment;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;

/**
 * Created at 10:56 AM on 1/14/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ImpContactsFragment extends BaseFragment {

    private DatabaseReference reference;
    private ChildEventAdapter listener;
    private ContactCategoriesAdapter adapter;

    public ImpContactsFragment() {
    }

    public static ImpContactsFragment newInstance() {
        return new ImpContactsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_imp_contacts, container, false);
        ListView categoriesListView = rootView.findViewById(R.id.contact_categories_list);
        final Context context = getContext() == null ? inflater.getContext() : getContext();

        adapter = new ContactCategoriesAdapter(context);
        categoriesListView.setAdapter(adapter);
        categoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactCategory category = adapter.getItem(position);
                if (category != null) {
                    startActivity(new Intent(context, ContactCategoryActivity.class)
                            .putExtra(ContactCategoryActivity.EXTRA_CATEGORY_ID, category.id)
                            .putExtra(ContactCategoryActivity.EXTRA_CATEGORY_TITLE, category.name));
                }
            }
        });

        loadCategories(context);
        return rootView;
    }

    private void loadCategories(Context context) {
        listener = new ChildEventAdapter(context) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ContactCategory category = ContactCategory.from(dataSnapshot);
                if (category == null) return;
                adapter.add(category);
                onLoad(adapter.getCount());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ContactCategory category = ContactCategory.from(dataSnapshot);
                if (category == null) return;
                adapter.remove(category);
                onLoad(adapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ContactCategory category = ContactCategory.from(dataSnapshot);
                if (category == null) return;
                int index = adapter.getPosition(category);
                if (index < 0) return;
                ContactCategory existing = adapter.getItem(index);
                if (existing == null) return;
                existing.set(category);
                adapter.notifyDataSetChanged();
            }
        };
        reference = FirebaseDatabase.getInstance().getReference("tbl_important_contact_categories");
        startLoading(reference, listener);
    }

    @Override
    public void onDestroy() {
        if (reference != null) reference.removeEventListener(listener);
        super.onDestroy();
    }

    private class ContactCategoriesAdapter extends ArrayAdapter<ContactCategory> {

        private ContactCategoriesAdapter(@NonNull Context context) {
            super(context, R.layout.item_contact_category, new ArrayList<ContactCategory>());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Context context = getContext();
            View rootView = convertView;
            if (rootView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                rootView = inflater.inflate(R.layout.item_contact_category, parent, false);
            }
            ContactCategory category = getItem(position);
            if (category == null) return rootView;
            TextView nameView = rootView.findViewById(R.id.contact_title_text);
            nameView.setText(category.name);
            return rootView;
        }

    }

}
