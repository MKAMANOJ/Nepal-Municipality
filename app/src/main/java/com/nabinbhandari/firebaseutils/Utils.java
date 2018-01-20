package com.nabinbhandari.firebaseutils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.nabinbhandari.municipality.BuildConfig;

/**
 * Created at 7:46 PM on 12/31/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
public class Utils {

    public static void onDatabaseError(Context context, DatabaseError error) {
        String message;
        if (error == null) {
            message = "null";
        } else {
            message = error.getMessage() + "\n" + error.getCode() + ": " + error.getDetails();
            if (BuildConfig.DEBUG) error.toException().printStackTrace();
        }
        if (context == null) return;
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
