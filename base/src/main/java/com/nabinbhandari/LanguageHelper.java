package com.nabinbhandari;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageHelper {

    public static final Locale NP = new Locale("np");

    private static void setAppLocale(String language, Activity activity) {

        /*Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(new Locale(language));
        activity.getApplicationContext().createConfigurationContext(configuration);*/

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = activity.getResources().getConfiguration();
        config.locale = locale;
        activity.getResources().updateConfiguration(config,
                activity.getResources().getDisplayMetrics());
        Locale.setDefault(locale);
    }

    public static void refreshLanguage(Activity activity) {
        setAppLocale(isNepali(activity) ? "np" : "en", activity);
    }

    public static void updatePrefs(Context context, boolean nepali) {
        SharedPreferences preference = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        preference.edit().putBoolean("nepali", nepali).apply();
    }

    public static boolean isNepali(Context context) {
        SharedPreferences preference = context.getSharedPreferences("language", Context.MODE_PRIVATE);
        return preference.getBoolean("nepali", false);
    }

}
