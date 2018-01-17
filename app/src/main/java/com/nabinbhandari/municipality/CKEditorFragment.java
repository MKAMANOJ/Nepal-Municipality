package com.nabinbhandari.municipality;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.firebaseutils.ValueEventAdapter;

/**
 * Created at 11:19 PM on 1/15/2018.
 *
 * @author bnabin51@gmail.com
 */

public class CKEditorFragment extends Fragment {

    private String dbLocation;
    private DatabaseReference reference;
    private ValueEventListener listener;

    public CKEditorFragment() {
    }

    @SuppressWarnings("SameParameterValue")
    public static CKEditorFragment newInstance(String dbLocation) {
        CKEditorFragment fragment = new CKEditorFragment();
        fragment.dbLocation = dbLocation;
        return fragment;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context context = getContext() == null ? inflater.getContext() : getContext();
        WebView rootView = new WebView(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        rootView.setPadding(padding, padding, padding, padding);
        rootView.getSettings().setBuiltInZoomControls(true);
        rootView.getSettings().setDisplayZoomControls(false);
        rootView.getSettings().setJavaScriptEnabled(true);
        loadData(context, rootView);
        return rootView;
    }

    private void loadData(Context context, final WebView webView) {
        listener = new ValueEventAdapter(context) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data;
                try {
                    data = dataSnapshot.getValue(String.class);
                } catch (Exception e) {
                    data = "<strong>Invalid data!</strong>";
                }
                if (data == null) data = "<strong>Data currently unavailable!</strong>";
                webView.loadData(data, "text/html", "UTF-8");
            }
        };
        reference = FirebaseDatabase.getInstance().getReference(dbLocation);
        reference.addValueEventListener(listener);
    }

    @Override
    public void onDestroy() {
        reference.removeEventListener(listener);
        super.onDestroy();
    }

}
