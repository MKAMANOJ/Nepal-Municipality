package com.nabinbhandari.firebaseutils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nabinbhandari.municipality.BuildConfig;
import com.nabinbhandari.municipality.R;

/**
 * Helper class for Firebase remote config.
 * <p>
 * Created on 11/30/17.
 *
 * @author bnabin51@gmail.com
 */
public class RemoteConfig {

    /**
     * This method should be called during the start of app to ensure the proper synchronization
     * of configs.
     */
    public static void start() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setDefaults(R.xml.local_config);

        int cacheExpirationSeconds = 7200;
        if (BuildConfig.DEBUG) {
            FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(true).build();
            remoteConfig.setConfigSettings(settings);
            cacheExpirationSeconds = 0;
        }

        remoteConfig.fetch(cacheExpirationSeconds)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.err.println("config fetched.");
                            remoteConfig.activateFetched();
                        } else {
                            System.err.println("config fetch failed.");
                            Exception exception = task.getException();
                            if (exception != null) printStackTrace(exception);
                        }
                    }
                });
    }

    public static int getMenuIconColor() {
        try {
            String colorStr = FirebaseRemoteConfig.getInstance().getString("menu_icon_color");
            if (TextUtils.isEmpty(colorStr)) throw new Exception();
            return Color.parseColor(colorStr);
        } catch (Throwable t) {
            printStackTrace(t);
            return 0x3F51B5;
        }
    }

    public static int getMenuTextColor() {
        try {
            String colorStr = FirebaseRemoteConfig.getInstance().getString("menu_text_color");
            if (TextUtils.isEmpty(colorStr)) throw new Exception();
            return Color.parseColor(colorStr);
        } catch (Throwable t) {
            printStackTrace(t);
            return 0x333333;
        }
    }

    private static void printStackTrace(Throwable t) {
        if (BuildConfig.DEBUG) t.printStackTrace();
    }

}
