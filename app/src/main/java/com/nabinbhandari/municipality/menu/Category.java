package com.nabinbhandari.municipality.menu;

import android.support.annotation.DrawableRes;

import com.nabinbhandari.LanguageHelper;
import com.nabinbhandari.municipality.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created at 7:40 PM on 1/6/2018.
 *
 * @author bnabin51@gmail.com
 */

@SuppressWarnings("WeakerAccess")
public class Category implements Serializable {

    private static ArrayList<Category> categories;

    static {
        categories = new ArrayList<>();
        categories.add(new Category(13, "", "Introduction", "परिचय", R.drawable.ic_introduction));
        categories.add(new Category(10, "", "Staff Details", "कर्मचारी विवरणहरू", R.drawable.ic_staffs));
        categories.add(new Category(11, "", "Gallery", "ग्यालरी", R.drawable.ic_gallery));
        categories.add(new Category(1, "tbl_news", "Notice / News", "सूचना / समाचार", R.drawable.ic_news));
        categories.add(new Category(2, "tbl_citizen_charter", "Citizen Charter", "नागरिक वडापत्र ", R.drawable.ic_citizen));
        categories.add(new Category(3, "tbl_downloads", "Downloads", "डाउनलोड", R.drawable.ic_downloads));
        categories.add(new Category(4, "tbl_public_procurement", "Public Procurement / Bidding", "सार्वजनिक खरीद / बिडिंग", R.drawable.ic_bid));
        categories.add(new Category(5, "tbl_planning_project", "Planning and Project", "योजना र परियोजना", R.drawable.ic_plan));
        categories.add(new Category(6, "tbl_budget_program", "Budget and Program", "बजेट र कार्यक्रम", R.drawable.ic_budget));
        categories.add(new Category(7, "tbl_tax_fee", "Tax and Fees", "कर र शुल्कहरू", R.drawable.ic_tax));
        categories.add(new Category(8, "tbl_law_regulation", "Laws and Regulations", "कानून र नियम", R.drawable.ic_rules));
        categories.add(new Category(12, "", "Important Contacts", "महत्त्वपूर्ण सम्पर्कहरू", R.drawable.ic_contacts));
        categories.add(new Category(9, "tbl_ward_profile", "Ward Profile", "वार्ड प्रोफाइल", R.drawable.ic_team));
        categories.add(new Category(14, "", "Office Contact", "कार्यालय सम्पर्क", R.drawable.ic_contact_us));
    }

    public static String findSlugById(int id) {
        for (Category category : categories) {
            if (category.id == id) {
                return category.slug;
            }
        }
        return "";
    }

    public final int id;
    public final String slug;
    public final String title_en;
    public final String title_np;
    public final int resId;

    private Category(int id, String slug, String title_en, String title_np, @DrawableRes int resId) {
        this.id = id;
        this.slug = slug;
        this.title_en = title_en;
        this.title_np = title_np;
        this.resId = resId;
    }

    @Override
    public String toString() {
        return Locale.getDefault().equals(LanguageHelper.NP) ? title_np : title_en;
    }

    public static ArrayList<Category> getDummyList() {
        return categories;
    }

}
