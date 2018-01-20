package com.nabinbhandari.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.nabinbhandari.municipality.CKEditorFragment;
import com.nabinbhandari.municipality.MainActivity;
import com.nabinbhandari.municipality.R;

/**
 * Created at 8:20 PM on 1/20/2018.
 *
 * @author bnabin51@gmail.com
 */

public class NotificationActivity extends AppCompatActivity {

    public static final String KEY_DB_LOCATION = "db_location";
    public static final String KEY_TITLE = "title";
    String dbLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbLocation = getIntent().getStringExtra(KEY_DB_LOCATION);
        if (dbLocation == null) {
            finish();
            return;
        }
        if (getIntent().hasExtra(KEY_TITLE)) {
            setTitle(getIntent().getStringExtra(KEY_TITLE));
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FrameLayout rootView = new FrameLayout(this);
        setContentView(rootView);
        rootView.setId(R.id.fragment_holder);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder,
                CKEditorFragment.newInstance(dbLocation)).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (MainActivity.isBackground) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

}
