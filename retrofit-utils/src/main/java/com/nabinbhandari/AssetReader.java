package com.nabinbhandari;

import android.content.Context;

import com.nabinbhandari.retrofit.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created at 7:38 PM on 12/26/2017.
 *
 * @author bnabin51@gmail.com
 */

public class AssetReader {

    public static String readStringAsset(Context context, String fileName, String defValue) {
        try {
            InputStream inStream = context.getAssets().open(fileName);
            StringBuilder builder = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defValue;
    }

}
