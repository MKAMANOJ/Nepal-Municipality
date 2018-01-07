package com.nabinbhandari.municipality.staffs;

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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabinbhandari.firebaseutils.ChildEventAdapter;
import com.nabinbhandari.municipality.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 8:19 PM on 1/7/2018.
 *
 * @author bnabin51@gmail.com
 */

public class StaffsFragment extends Fragment {

    private List<Staff> staffs = new ArrayList<>();
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
        staffsAdapter = new StaffsAdapter(context, staffs);
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
                staffs.add(staff);
                staffsAdapter.notifyDataSetChanged();
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

        StaffsAdapter(@NonNull Context context, @NonNull List<Staff> staffs) {
            super(context, R.layout.item_staff, staffs);
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

            Staff staff = getItem(position);
            if (staff == null) return view;
            TextView nameTextView = view.findViewById(R.id.nameTextView);
            nameTextView.setText(staff.name);
            return view;
        }

    }

}
