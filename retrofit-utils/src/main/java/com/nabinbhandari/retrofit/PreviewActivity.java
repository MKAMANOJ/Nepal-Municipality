package com.nabinbhandari.retrofit;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGES = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_preview);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        @SuppressWarnings("unchecked")
        ArrayList<Image> images = (ArrayList<Image>) getIntent().getSerializableExtra(EXTRA_IMAGES);

        viewPager.setAdapter(new ImagesAdapter(getSupportFragmentManager(), images));
    }

}
