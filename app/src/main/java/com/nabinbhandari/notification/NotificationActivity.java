package com.nabinbhandari.notification;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.nabinbhandari.municipality.CKEditorFragment;
import com.nabinbhandari.municipality.R;

/**
 * Created at 8:20 PM on 1/20/2018.
 *
 * @author bnabin51@gmail.com
 */

public class NotificationActivity extends AppCompatActivity {

    public static final String KEY_DB_LOCATION = "db_location";
    String dbLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbLocation = getIntent().getStringExtra(KEY_DB_LOCATION);
        if (dbLocation == null) {
            finish();
            return;
        }
        FrameLayout rootView = new FrameLayout(this);
        setContentView(rootView);
        rootView.setId(R.id.fragment_holder);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                CKEditorFragment.newInstance(dbLocation)).commit();
    }

}
