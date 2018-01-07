package com.nabinbhandari.firebaseutils;

import android.content.Context;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created at 8:43 PM on 12/31/2017.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
public abstract class ChildEventAdapter implements ChildEventListener {

    private Context mContext;

    public ChildEventAdapter(Context context) {
        mContext = context;
    }

    @Override
    public abstract void onChildAdded(DataSnapshot dataSnapshot, String s);

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Utils.onDatabaseError(mContext, databaseError);
    }

}
