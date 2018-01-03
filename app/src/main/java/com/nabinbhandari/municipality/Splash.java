package com.nabinbhandari.municipality;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created at 9:01 PM on 1/3/2018.
 *
 * @author bnabin51@gmail.com
 */

public class Splash extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    private Runnable splashRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.layout_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSplash();
    }

    private void startSplash() {
        final ImageView imageView = findViewById(R.id.splashImageView);
        imageView.setVisibility(View.INVISIBLE);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.startAnimation(AnimationUtils.
                        loadAnimation(Splash.this, android.R.anim.fade_in));
                imageView.setVisibility(View.VISIBLE);
            }
        }, 300);

        splashRunnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, MainActivity.class));
                finish();
            }
        };
        findViewById(R.id.welcomeTextView).postDelayed(splashRunnable, SPLASH_DELAY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        findViewById(R.id.splashImageView).removeCallbacks(splashRunnable);
    }

}
