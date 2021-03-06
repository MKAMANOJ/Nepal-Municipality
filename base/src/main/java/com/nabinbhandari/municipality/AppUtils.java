package com.nabinbhandari.municipality;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created at 11:59 PM on 1/11/2018.
 *
 * @author bnabin51@gmail.com
 */

public class AppUtils {

    public static void setPhoneNumber(final TextView editText, final String text, final String phone) {
        Spannable span = new SpannableString(text);
        ForegroundColorSpan color = new ForegroundColorSpan(ContextCompat
                .getColor(editText.getContext(), R.color.colorAccent));

        ClickableSpan click = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                editText.getContext().startActivity(intent);
            }
        };

        try {
            int textLength = text.length(), phoneLength = phone.length();
            span.setSpan(color, textLength - phoneLength, textLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            span.setSpan(click, textLength - phoneLength, textLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            editText.setText(span);
            editText.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Throwable t) {
            editText.setText(text);
        }
    }

    static void openPlayStore(Context context, String errorMessage) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());

        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, uri);
        playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(playStoreIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    public static void log(String message) {
        if (BuildConfig.DEBUG) System.err.println(message);
    }

    public static void printStackTrace(Throwable t) {
        if (BuildConfig.DEBUG) t.printStackTrace();
    }

    @SuppressWarnings({"WeakerAccess", "SameParameterValue"})
    public static String readStringAsset(Context context, String fileName, String defValue) {
        try {
            InputStream inStream = context.getAssets().open(fileName);
            StringBuilder builder = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defValue;
    }

}
