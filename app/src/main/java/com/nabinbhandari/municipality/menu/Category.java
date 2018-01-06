package com.nabinbhandari.municipality.menu;

import android.support.annotation.DrawableRes;

import com.nabinbhandari.municipality.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created at 7:40 PM on 1/6/2018.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
public class Category {

    public final int id;
    public final String title_en;
    public final String title_np;
    public final int resId;

    public Category(int id, String title_en, String title_np, @DrawableRes int resId) {
        this.id = id;
        this.title_en = title_en;
        this.title_np = title_np;
        this.resId = resId;
    }

    @Override
    public String toString() {
        return title_en;
    }

    public static List<Category> getDummyList() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Introduction", "Introduction (N)", R.drawable.ic_introduction));
        categories.add(new Category(2, "Citizen Charter", "Citizen Charter (N)", R.drawable.ic_citizen));
        categories.add(new Category(3, "Staff Details", "Staff Details (N)", R.drawable.ic_staffs));
        categories.add(new Category(4, "Notice / News", "Notice / News (N)", R.drawable.ic_news));
        categories.add(new Category(5, "Downloads", "Downloads (N)", R.drawable.ic_downloads));
        categories.add(new Category(6, "Public Procurement / Bidding", "Public Procurement / Bidding (N)", R.drawable.ic_bid));
        categories.add(new Category(7, "Gallery", "Gallery (N)", R.drawable.ic_gallery));
        categories.add(new Category(8, "Planning and Project", "Planning and Project (N)", R.drawable.ic_plan));
        categories.add(new Category(9, "Budget and Program", "Budget (N)", R.drawable.ic_budget));
        categories.add(new Category(10, "Important Contacts", "Contacts (N)", R.drawable.ic_contacts));
        categories.add(new Category(11, "Tax and Fees", "Tax and Fees (N)", R.drawable.ic_tax));
        categories.add(new Category(12, "Laws and Regulations", "Laws and Regulations(N)", R.drawable.ic_rules));
        categories.add(new Category(13, "Ward Profile", "Ward Profile (N)", R.drawable.ic_team));
        categories.add(new Category(14, "Contact Us", "Contact Us(N)", R.drawable.ic_contact_us));
        return categories;
    }

}
