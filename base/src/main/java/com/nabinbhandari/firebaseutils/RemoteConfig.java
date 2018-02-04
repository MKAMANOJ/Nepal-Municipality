package com.nabinbhandari.firebaseutils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

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
                            log("config fetched.");
                            remoteConfig.activateFetched();
                        } else {
                            log("config fetch failed.");
                            Exception exception = task.getException();
                            if (exception != null) printStackTrace(exception);
                        }
                    }
                });
    }

    public static String getContactEmail() {
        try {
            String contactEmail = FirebaseRemoteConfig.getInstance().getString("contact_email");
            if (Patterns.EMAIL_ADDRESS.matcher(contactEmail).matches()) {
                return contactEmail;
            }
        } catch (Throwable ignored) {
        }
        return "mka@citizeninfotech.com.np";
    }

    public static int getMenuIconColor() {
        return getColor("menu_icon_color", 0x009688);
    }

    public static int getMenuTextColor() {
        return getColor("menu_text_color", 0x333333);
    }

    public static int getMenuBackgroundColor() {
        return getColor("menu_background_color", 0xf5f5f5);
    }

    private static int getColor(String key, int defaultColor) {
        try {
            String colorStr = FirebaseRemoteConfig.getInstance().getString(key);
            if (TextUtils.isEmpty(colorStr)) throw new Exception();
            return Color.parseColor(colorStr);
        } catch (Throwable t) {
            printStackTrace(t);
            return defaultColor;
        }
    }

    private static void printStackTrace(Throwable t) {
        if (BuildConfig.DEBUG) t.printStackTrace();
    }

    private static void log(String message) {
        if (BuildConfig.DEBUG) System.err.println(message);
    }

}
