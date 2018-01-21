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

    private WebView rootView;
    private Runnable noInternetRunnable;

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
        rootView = new WebView(context);
        int margin = context.getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(margin, margin, margin, margin);
        rootView.setLayoutParams(params);
        rootView.getSettings().setBuiltInZoomControls(true);
        rootView.getSettings().setDisplayZoomControls(false);
        rootView.getSettings().setJavaScriptEnabled(true);
        loadData(context);
        return rootView;
    }

    private void loadData(Context context) {
        loadHTML(getString(R.string.loading));
        noInternetRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    loadHTML(getString(R.string.error_internet_problem));
                } catch (Throwable ignored) {
                }
            }
        };
        rootView.postDelayed(noInternetRunnable, 5000);

        listener = new ValueEventAdapter(context) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rootView.removeCallbacks(noInternetRunnable);
                String data;
                try {
                    data = dataSnapshot.getValue(String.class);
                } catch (Exception e) {
                    data = "<strong>Invalid data!</strong>";
                }
                if (data == null) data = "<strong>Data currently unavailable!</strong>";
                loadHTML(data);
            }
        };

        reference = FirebaseDatabase.getInstance().getReference(dbLocation);
        reference.addValueEventListener(listener);
    }

    private void loadHTML(String html) {
        rootView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    @Override
    public void onDestroy() {
        if (rootView != null) rootView.removeCallbacks(noInternetRunnable);
        if (reference != null) reference.removeEventListener(listener);
        super.onDestroy();
    }

}
