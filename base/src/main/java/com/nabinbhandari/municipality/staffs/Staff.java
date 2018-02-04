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
    public String email;
    public String image;
    public String address;
    public String designation;
    public String office_phone;
    public String personal_phone;
    public String appointment_date;

    public String key;
    public boolean selected;

    @Nullable
    public static Staff from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        Staff staff = dataSnapshot.getValue(Staff.class);
        if (staff != null) {
            staff.key = dataSnapshot.getKey();
        }
        return staff;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Staff && ((Staff) obj).id == this.id;
    }

    public void set(Staff staff) {
        this.id = staff.id;
        this.key = staff.key;
        this.name = staff.name;
        this.email = staff.email;
        this.image = staff.image;
        this.address = staff.address;
        //this.selected = staff.selected;
        this.designation = staff.designation;
        this.office_phone = staff.office_phone;
        this.personal_phone = staff.personal_phone;
        this.appointment_date = staff.appointment_date;
    }

}
