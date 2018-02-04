package com.nabinbhandari.municipality;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created at 12:10 AM on 1/21/2018.
 *
 * @author bnabin51@gmail.com
 */

public class LicenseFragment extends Fragment {

    public LicenseFragment() {
    }

    public static LicenseFragment newInstance() {
        return new LicenseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Context context = getContext() == null ? inflater.getContext() : getContext();
        WebView webView = new WebView(context);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        int margin = context.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin);
        params.setMargins(margin, margin, margin, margin);
        webView.setLayoutParams(params);
        String text = AppUtils.readStringAsset(context, "license.html", "");
        webView.loadDataWithBaseURL(null, text, "text/html", "UTF-8", null);
        return webView;
    }

}
