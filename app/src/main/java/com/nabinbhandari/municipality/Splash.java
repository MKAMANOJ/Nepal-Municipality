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
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nabinbhandari.LanguageHelper;

/**
 * Created at 9:01 PM on 1/3/2018.
 *
 * @author bnabin51@gmail.com
 */

public class Splash extends AppCompatActivity {

    public static final String KEY_MESSAGE = "message";

    static {
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

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
        LanguageHelper.refreshLanguage(this);
        setContentView(R.layout.layout_splash);

        if (getIntent().hasExtra(KEY_MESSAGE)) {
            String message = getIntent().getStringExtra(KEY_MESSAGE);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        FirebaseMessaging.getInstance().subscribeToTopic(getPackageName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        findViewById(R.id.splashImageView).removeCallbacks(splashRunnable);
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

}
