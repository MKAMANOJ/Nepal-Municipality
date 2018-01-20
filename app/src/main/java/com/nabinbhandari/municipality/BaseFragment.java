package com.nabinbhandari.municipality;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.nabinbhandari.firebaseutils.ValueEventAdapter;

/**
 * Created at 12:24 PM on 1/20/2018.
 *
 * @author bnabin51@gmail.com
 */

public abstract class BaseFragment extends Fragment {

    private ProgressBar loadingProgress;
    private TextView loadingMessageView;

    public BaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context context = getContext() == null ? inflater.getContext() : getContext();

        FrameLayout frameLayout = new FrameLayout(context);
        View loadingView = inflater.inflate(R.layout.layout_loading, frameLayout, false);
        loadingProgress = loadingView.findViewById(R.id.loading_progress);
        loadingMessageView = loadingView.findViewById(R.id.loading_message_text);

        View contentView = onCreateView(inflater, container);
        frameLayout.addView(contentView);
        frameLayout.addView(loadingView);

        return frameLayout;
    }

    protected abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    protected void startLoading(final Query databaseReference, final ChildEventListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventAdapter(getContext()) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                onLoad(count);
                databaseReference.addChildEventListener(listener);
            }
        });
    }

    protected void onLoad(long count) {
        loadingProgress.setVisibility(View.GONE);
        if (count == 0) {
            loadingMessageView.setVisibility(View.VISIBLE);
            loadingMessageView.setText(R.string.available_soon);
        } else {
            loadingMessageView.setVisibility(View.GONE);
        }
    }

    protected void setLoadingTextColor(int textColor) {
        loadingMessageView.setTextColor(textColor);
    }

}
