package com.nabinbhandari.notification;

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
import java.util.Collections;
import java.util.Comparator;

/**
 * Created at 9:47 PM on 1/20/2018.
 *
 * @author bnabin51@gmail.com
 */

public class NotificationsFragment extends BaseFragment {

    private NotificationsAdapter adapter;
    private DatabaseReference dbReference;
    private ChildEventAdapter listener;

    public NotificationsFragment() {
    }

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        final Context context = getContext() == null ? inflater.getContext() : getContext();
        ListView listView = new ListView(context);
        listView.setSelector(android.R.color.transparent);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        listView.setPadding(padding / 2, padding, padding / 2, padding);
        listView.setDivider(null);

        adapter = new NotificationsAdapter(context, new ArrayList<NotificationContent>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationContent content = adapter.getItem(position);
                if (content == null) return;
                String dbLocation = "tbl_push_notifications/" + content.key + "/message";
                startActivity(new Intent(context, NotificationActivity.class)
                        .putExtra(NotificationActivity.KEY_TITLE, content.title)
                        .putExtra(NotificationActivity.KEY_DB_LOCATION, dbLocation));
            }
        });
        loadNotifications();
        return listView;
    }

    public void loadNotifications() {
        dbReference = FirebaseDatabase.getInstance().getReference("tbl_push_notifications");
        listener = new ChildEventAdapter(getContext()) {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                NotificationContent content = NotificationContent.from(dataSnapshot);
                if (content == null) return;
                adapter.add(content);
                onLoad(adapter.getCount());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                NotificationContent content = NotificationContent.from(dataSnapshot);
                if (content == null) return;
                adapter.remove(content);
                onLoad(adapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                NotificationContent content = NotificationContent.from(dataSnapshot);
                if (content == null) return;
                int index = adapter.getPosition(content);
                if (index < 0) return;
                NotificationContent existing = adapter.getItem(index);
                if (existing == null) return;
                existing.set(content);
                adapter.notifyDataSetChanged();
            }
        };
        startLoading(dbReference, listener);
    }

    @Override
    public void onDestroy() {
        if (dbReference != null) dbReference.removeEventListener(listener);
        super.onDestroy();
    }

    private class NotificationsAdapter extends ArrayAdapter<NotificationContent> {

        private ArrayList<NotificationContent> notifications;

        NotificationsAdapter(@NonNull Context context, ArrayList<NotificationContent> notifications) {
            super(context, R.layout.item_content, notifications);
            this.notifications = notifications;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View rootView = convertView;
            if (rootView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                rootView = inflater.inflate(R.layout.item_content, parent, false);
            }

            NotificationContent content = getItem(position);
            if (content == null) return rootView;

            TextView titleTextView = rootView.findViewById(R.id.content_title_text);
            TextView descTextView = rootView.findViewById(R.id.content_desc_text);

            titleTextView.setText(content.title);
            descTextView.setText(content.description);

            return rootView;
        }

        @Override
        public void notifyDataSetChanged() {
            Collections.sort(notifications, new Comparator<NotificationContent>() {
                @Override
                public int compare(NotificationContent o1, NotificationContent o2) {
                    if (o2.updated_at == null) return 0;
                    return o2.updated_at.compareTo(o1.updated_at);
                }
            });
            super.notifyDataSetChanged();
        }

    }

}
