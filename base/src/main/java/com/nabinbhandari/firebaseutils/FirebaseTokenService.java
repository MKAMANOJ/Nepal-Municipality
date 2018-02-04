package com.nabinbhandari.firebaseutils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created on 7/21/17.
 *
 * @author bnabin51@gmail.com
 */
public class FirebaseTokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Firebase", "Refreshed token: " + refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic(getPackageName());
    }

}
