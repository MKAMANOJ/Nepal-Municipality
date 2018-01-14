package com.nabinbhandari.municipality.impcontacts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;

/**
 * Created at 10:56 AM on 1/14/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ImpContactsFragment extends Fragment {

    private DatabaseReference reference;
    private ChildEventAdapter listener;
    private ContactCategoriesAdapter adapter;

    public ImpContactsFragment() {
    }

    public static ImpContactsFragment newInstance() {
        return new ImpContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_imp_contacts, container, false);
        ListView categoriesListView = rootView.findViewById(R.id.contact_categories_list);
        Context context = getContext() == null ? inflater.getContext() : getContext();
        adapter = new ContactCategoriesAdapter(context);
        categoriesListView.setAdapter(adapter);
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
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ContactCategory category = ContactCategory.from(dataSnapshot);
                if (category == null) return;
                adapter.remove(category);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ContactCategory category = ContactCategory.from(dataSnapshot);
                if (category == null) return;
                int index = adapter.getPosition(category);
                ContactCategory existing = adapter.getItem(index);
                if (existing == null) return;
                existing.set(category);
            }
        };
        reference = FirebaseDatabase.getInstance().getReference("tbl_important_contact_categories");
        reference.addChildEventListener(listener);
    }

    @Override
    public void onDestroy() {
        reference.removeEventListener(listener);
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
