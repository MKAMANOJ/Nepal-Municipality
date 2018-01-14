package com.nabinbhandari.municipality.impcontacts;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.AppUtils;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;

/**
 * Created at 2:15 PM on 1/14/2018.
 *
 * @author bnabin51@gmail.com
 */

public class ContactCategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_CATEGORY_TITLE = "category_title";

    private ContactAdapter adapter;
    private Query reference;
    private ChildEventAdapter listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int categoryId = getIntent().getIntExtra(EXTRA_CATEGORY_ID, 0);
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

        ListView listView = new ListView(this);
        int padding = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        listView.setPadding(padding, padding, padding, padding);
        listView.setDivider(null);
        adapter = new ContactAdapter(this);
        listView.setAdapter(adapter);
        setContentView(listView);
        loadContacts(categoryId);
    }

    private void loadContacts(int categoryId) {
        reference = FirebaseDatabase.getInstance().getReference("tbl_important_contacts")
                .orderByChild("important_contact_category_id")
                .equalTo(categoryId);
        listener = new ChildEventAdapter(this) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ImpContact contact = ImpContact.from(dataSnapshot);
                if (contact == null) return;
                adapter.add(contact);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ImpContact contact = ImpContact.from(dataSnapshot);
                if (contact == null) return;
                adapter.remove(contact);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ImpContact contact = ImpContact.from(dataSnapshot);
                if (contact == null) return;
                int index = adapter.getPosition(contact);
                if (index < 0) return;
                ImpContact existing = adapter.getItem(index);
                if (existing == null) return;
                existing.set(contact);
                adapter.notifyDataSetChanged();
            }
        };
        reference.addChildEventListener(listener);
    }

    @Override
    protected void onDestroy() {
        if (listener != null) reference.removeEventListener(listener);
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

    private static class ContactAdapter extends ArrayAdapter<ImpContact> {

        private static final String BASE = "http://palika.engineeringinnepal.com/palika/storage/";

        ContactAdapter(@NonNull Context context) {
            super(context, 0, new ArrayList<ImpContact>());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Context context = parent.getContext();
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.item_contact, parent, false);
            }

            final ImpContact contact = getItem(position);
            if (contact == null) return view;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean selected = contact.selected;
                    int count = getCount();
                    for (int i = 0; i < count; i++) {
                        ImpContact otherContact = getItem(i);
                        if (otherContact != null) otherContact.selected = false;
                    }
                    contact.selected = !selected;
                    notifyDataSetChanged();
                }
            });

            TextView nameTextView = view.findViewById(R.id.nameTextView);
            nameTextView.setText(contact.name);
            TextView designationTextView = view.findViewById(R.id.designationTextView);
            designationTextView.setText(contact.designation);

            ImageView imageView = view.findViewById(R.id.imagePreview);
            if (TextUtils.isEmpty(contact.image)) {
                imageView.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.placeholder_warning));
            } else {
                Glide.with(imageView).load(BASE + contact.image).into(imageView);
            }

            TextView addressTextView = view.findViewById(R.id.addressTextView);
            TextView emailTextView = view.findViewById(R.id.emailTextView);
            TextView phoneTextView = view.findViewById(R.id.phoneTextView);
            TextView mobileTextView = view.findViewById(R.id.mobileTextView);
            TextView faxTextView = view.findViewById(R.id.faxTextView);
            TextView servicesTextView = view.findViewById(R.id.servicesTextView);

            if (TextUtils.isEmpty(contact.address)) {
                addressTextView.setVisibility(View.GONE);
            } else {
                addressTextView.setText(String.format(context.getString(R.string.format_address),
                        contact.address));
                addressTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(contact.email)) {
                emailTextView.setVisibility(View.GONE);
            } else {
                emailTextView.setText(String.format(context.getString(R.string.format_email),
                        contact.email));
                emailTextView.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                emailTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(contact.phone)) {
                phoneTextView.setVisibility(View.GONE);
            } else {
                String text = String.format(context
                        .getString(R.string.format_phone), contact.phone);
                AppUtils.setPhoneNumber(phoneTextView, text, contact.phone);
                phoneTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(contact.mobile)) {
                mobileTextView.setVisibility(View.GONE);
            } else {
                String text = String.format(context
                        .getString(R.string.format_mobile), contact.mobile);
                AppUtils.setPhoneNumber(mobileTextView, text, contact.mobile);
                mobileTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(contact.fax)) {
                faxTextView.setVisibility(View.GONE);
            } else {
                String text = String.format(context
                        .getString(R.string.format_fax), contact.mobile);
                AppUtils.setPhoneNumber(faxTextView, text, contact.mobile);
                faxTextView.setVisibility(View.VISIBLE);
            }


            if (TextUtils.isEmpty(contact.service)) {
                servicesTextView.setVisibility(View.GONE);
            } else {
                servicesTextView.setText(String.format(context
                        .getString(R.string.format_services), contact.service));
                servicesTextView.setVisibility(View.VISIBLE);
            }

            view.findViewById(R.id.detailsLayout)
                    .setVisibility(contact.selected ? View.VISIBLE : View.GONE);

            return view;
        }

    }

}
