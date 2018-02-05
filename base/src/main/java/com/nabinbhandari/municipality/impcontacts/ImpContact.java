package com.nabinbhandari.municipality.impcontacts;

import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created at 8:27 PM on 1/7/2018.
 * <p/>
 * Model class for staff.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
@IgnoreExtraProperties
public class ImpContact implements Serializable {

    public int id;
    public String name;
    public String email;
    public String image;
    public String address;
    public String designation;
    public String phone;
    public String mobile;
    public String fax;
    public String service;

    public String key;
    public boolean selected;

    @Nullable
    public static ImpContact from(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return null;
        ImpContact contact = dataSnapshot.getValue(ImpContact.class);
        if (contact != null) {
            contact.key = dataSnapshot.getKey();
        }
        return contact;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ImpContact && ((ImpContact) obj).id == this.id;
    }

    public void set(ImpContact staff) {
        this.id = staff.id;
        this.key = staff.key;
        this.name = staff.name;
        this.email = staff.email;
        this.image = staff.image;
        this.address = staff.address;
        //this.selected = staff.selected; // intentionally commented.
        this.designation = staff.designation;
        this.phone = staff.phone;
        this.mobile = staff.mobile;
        this.fax = staff.fax;
        this.service = staff.service;
    }

}
