package com.nabinbhandari.municipality.contact;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.nabinbhandari.municipality.AppUtils;
import com.nabinbhandari.municipality.R;

/**
 * Created at 7:09 PM on 1/22/2018.
 *
 * @author bnabin51@gmail.com
 */

class EmailUtils {

    private static String getAppName(Context context) {
        String appName = "Municipality";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            ApplicationInfo aInfo = pm.getApplicationInfo(
                    context.getPackageName(), 0);
            appName = pm.getApplicationLabel(aInfo) + " v" + pInfo.versionName;
        } catch (Exception e) {
            AppUtils.printStackTrace(e);
        }
        return appName;
    }

    /**
     * Opens the Default email app on the device for sending feedback.
     *
     * @param context App context.
     */
    private static Intent getEmailComposer(Context context, String message) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{FirebaseRemoteConfig.getInstance()
                .getString("contact_email")});
        i.putExtra(Intent.EXTRA_SUBJECT, getAppName(context) + " - Contact");
        i.putExtra(Intent.EXTRA_TEXT, message);
        return i;
    }

    static void sendEmail(Context context, String fullName, String address,
                          String number, String email, String message) {
        String text = "Message: \n" + message +
                "\n\nName: " + fullName +
                "\nAddress: " + address +
                "\nNumber: " + number +
                "\nEmail: " + email;
        Intent intent = getEmailComposer(context, text);
        try {
            String emailAddress = FirebaseRemoteConfig.getInstance().getString("contact_email");
            context.startActivity(Intent.createChooser(intent,
                    context.getString(R.string.email_label) + " " + emailAddress));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "Email app not found!", Toast.LENGTH_LONG).show();
        }
    }

}
