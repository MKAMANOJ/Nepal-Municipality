package com.nabinbhandari.municipality.staffs;

import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created at 8:27 PM on 1/7/2018.
 * <p/>
 * Model class for staff.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
@IgnoreExtraProperties
public class Staff {

    public int id;
    public String name;

    public String key;

    @Nullable
    public static Staff from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        Staff staff = dataSnapshot.getValue(Staff.class);
        if (staff != null) {
            staff.key = dataSnapshot.getKey();
        }
        return staff;
    }

}
