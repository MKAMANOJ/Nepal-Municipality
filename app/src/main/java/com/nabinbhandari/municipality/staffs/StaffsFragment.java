package com.nabinbhandari.municipality.staffs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;

/**
 * Created at 8:19 PM on 1/7/2018.
 *
 * @author bnabin51@gmail.com
 */

public class StaffsFragment extends Fragment {

    private StaffsAdapter staffsAdapter;
    private DatabaseReference dbReference;
    private ChildEventListener staffsListener;

    public StaffsFragment() {
    }

    public static StaffsFragment newInstance() {
        return new StaffsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context context = inflater.getContext();
        ListView listView = new ListView(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        listView.setPadding(padding, padding, padding, padding);
        listView.setDivider(null);
        staffsAdapter = new StaffsAdapter(context);
        listView.setAdapter(staffsAdapter);
        loadStaffs(context);
        return listView;
    }

    private void loadStaffs(Context context) {
        dbReference = FirebaseDatabase.getInstance().getReference("tbl_staffs");
        staffsListener = new ChildEventAdapter(context) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Staff staff = Staff.from(dataSnapshot);
                if (staff == null) return;
                staffsAdapter.add(staff);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Staff staff = Staff.from(dataSnapshot);
                if (staff == null) return;
                int index = staffsAdapter.getPosition(staff);
                if (index < 0) return;
                Staff existing = staffsAdapter.getItem(index);
                if (existing != null) existing.set(staff);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Staff staff = Staff.from(dataSnapshot);
                staffsAdapter.remove(staff);
            }
        };
        dbReference.addChildEventListener(staffsListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbReference.removeEventListener(staffsListener);
    }

    private static class StaffsAdapter extends ArrayAdapter<Staff> {

        private static final String BASE = "http://manoj.engineeringinnepal.com/palika/storage/";

        StaffsAdapter(@NonNull Context context) {
            super(context, R.layout.item_staff, new ArrayList<Staff>());
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Context context = parent.getContext();
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.item_staff, parent, false);
            }

            final Staff staff = getItem(position);
            if (staff == null) return view;

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean selected = staff.selected;
                    int count = getCount();
                    for (int i = 0; i < count; i++) {
                        Staff otherStaff = getItem(i);
                        if (otherStaff != null) otherStaff.selected = false;
                    }
                    staff.selected = !selected;
                    notifyDataSetChanged();
                }
            });

            TextView nameTextView = view.findViewById(R.id.nameTextView);
            nameTextView.setText(staff.name);
            TextView designationTextView = view.findViewById(R.id.designationTextView);
            designationTextView.setText(staff.designation);

            ImageView imageView = view.findViewById(R.id.imagePreview);
            if (TextUtils.isEmpty(staff.image)) {
                imageView.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.placeholder_warning));
            } else {
                Glide.with(imageView).load(BASE + staff.image).into(imageView);
            }

            TextView addressTextView = view.findViewById(R.id.addressTextView);
            TextView emailTextView = view.findViewById(R.id.emailTextView);
            TextView officePhoneTextView = view.findViewById(R.id.officePhoneTextView);
            TextView personalPhoneTextView = view.findViewById(R.id.personalPhoneTextView);
            TextView appointmentDateTextView = view.findViewById(R.id.appointmentDateTextView);

            if (TextUtils.isEmpty(staff.address)) {
                addressTextView.setVisibility(View.GONE);
            } else {
                addressTextView.setText(String.format(context.getString(R.string.format_address),
                        staff.address));
                addressTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(staff.email)) {
                emailTextView.setVisibility(View.GONE);
            } else {
                emailTextView.setText(String.format(context.getString(R.string.format_email),
                        staff.email));
                emailTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(staff.office_phone)) {
                officePhoneTextView.setVisibility(View.GONE);
            } else {
                officePhoneTextView.setText(String.format(context
                        .getString(R.string.format_office_phone), staff.office_phone));
                officePhoneTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(staff.personal_phone)) {
                personalPhoneTextView.setVisibility(View.GONE);
            } else {
                personalPhoneTextView.setText(String.format(context
                        .getString(R.string.format_personal_phone), staff.personal_phone));
                personalPhoneTextView.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(staff.appointment_date)) {
                appointmentDateTextView.setVisibility(View.GONE);
            } else {
                appointmentDateTextView.setText(String.format(context
                        .getString(R.string.format_appointment_date), staff.appointment_date));
                appointmentDateTextView.setVisibility(View.VISIBLE);
            }

            view.findViewById(R.id.detailsLayout)
                    .setVisibility(staff.selected ? View.VISIBLE : View.GONE);

            return view;
        }

    }

}
