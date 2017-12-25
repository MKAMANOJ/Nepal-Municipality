package com.nabinbhandari.retrofit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
public class Utils {

    private static Gson gson;

    public static <T> T fromJson(String json, Class classOfT) {
        return getGson().fromJson(json, (Type) classOfT);
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().create();
        }
        return gson;
    }

    @NonNull
    public static String getErrorMessage(Response response) {
        System.err.println("Response: " + response.raw());
        ResponseBody responseBody = response.errorBody();
        try {
            String errorMessage;
            if (responseBody != null) {
                String errorBody = responseBody.string();
                System.err.println(errorBody);
                MyResponse myResponse = fromJson(errorBody,
                        MyResponse.class);
                errorMessage = myResponse == null || myResponse.message == null ?
                        "Error " + response.code() + ": " + response.message() : myResponse.message;
            } else errorMessage = "No data received.";
            try {
                throw new Exception("trace");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return errorMessage;
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
            return "Error " + response.code() + ": " + e.getMessage();
        }
    }

    /**
     * Helper method to show Toast from both UI/background thread.
     *
     * @param view    View to get the context from.
     * @param message Message to be shown in the Toast. Null value will be ignored.
     */
    public static void showToastOnUI(@NonNull final View view, @Nullable final String message) {
        if (message != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showProgress(Activity activity, final ProgressDialog progressDialog,
                                    final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    public static void hideProgress(final Activity activity, final ProgressDialog progressDialog,
                                    final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.hide();
                if (message != null) {
                    showToastOnUI(activity.getWindow().getDecorView(), message);
                }
            }
        });
    }

    public static void dismissProgress(final Activity activity, final ProgressDialog progressDialog,
                                       final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (message != null) {
                    showToastOnUI(activity.getWindow().getDecorView(), message);
                }
            }
        });
    }

}
