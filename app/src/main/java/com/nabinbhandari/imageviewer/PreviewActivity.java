package com.nabinbhandari.imageviewer;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.nabinbhandari.municipality.R;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGES = "images";
    public static final String EXTRA_INDEX = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_preview);
        ViewPager viewPager = findViewById(R.id.viewPager);

        try {
            @SuppressWarnings("unchecked")
            ArrayList<Image> images = (ArrayList<Image>) getIntent().getSerializableExtra(EXTRA_IMAGES);
            viewPager.setAdapter(new ImagesAdapter(getSupportFragmentManager(), images));

            int index = getIntent().getIntExtra(EXTRA_INDEX, 0);
            index = Math.max(index, 0);
            index = Math.min(index, images.size() - 1);
            viewPager.setCurrentItem(index);
        } catch (Throwable t) {
            t.printStackTrace();
            Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
