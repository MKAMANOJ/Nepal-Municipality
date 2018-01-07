package com.nabinbhandari.firebaseutils;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created at 8:30 PM on 12/31/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
public abstract class ValueEventAdapter implements ValueEventListener {

    private Context mContext;

    public ValueEventAdapter(Context context) {
        mContext = context;
    }

    @Override
    public abstract void onDataChange(DataSnapshot dataSnapshot);

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Utils.onDatabaseError(mContext, databaseError);
    }

}
