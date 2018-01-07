package com.nabinbhandari.municipality;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nabinbhandari.LanguageHelper;
import com.nabinbhandari.municipality.gallery.GalleryFragment;
import com.nabinbhandari.municipality.menu.Category;
import com.nabinbhandari.municipality.menu.MenuFragment;
import com.nabinbhandari.municipality.staffs.StaffsFragment;
import com.nabinbhandari.retrofit.ImageUtils;
import com.nabinbhandari.retrofit.PhotoService;
import com.nabinbhandari.retrofit.RetrofitUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuFragment.OnCategoryClickListener {

    private static final int NAV_ICON_COLOR = Color.BLUE;

    private FragmentManager fragmentManager;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.refreshLanguage(this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_home);
        if (drawable != null) drawable.setColorFilter(NAV_ICON_COLOR, PorterDuff.Mode.SRC_IN);
        menu.findItem(R.id.nav_home).setIcon(drawable);
        for (Category category : Category.getDummyList()) {
            menu.add(R.id.menuGroup, category.id, Menu.FIRST, category.toString());
            drawable = ContextCompat.getDrawable(this, category.resId);
            if (drawable != null) drawable.setColorFilter(NAV_ICON_COLOR, PorterDuff.Mode.SRC_IN);
            menu.findItem(category.id).setCheckable(true).setIcon(drawable);
        }

        PhotoService service = RetrofitUtils.getRetrofit().create(PhotoService.class);
        ImageUtils.init(this, service);

        fragmentManager = getSupportFragmentManager();
        MenuFragment menuFragment = MenuFragment.newInstance(Category.getDummyList(), this);
        fragmentManager.beginTransaction().replace(R.id.fragment_holder, menuFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.nepali);
        item.setChecked(LanguageHelper.isNepali(this));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.nepali) {
            item.setChecked(!item.isChecked());
            LanguageHelper.updatePrefs(this, item.isChecked());
            LanguageHelper.refreshLanguage(this);
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }
        } else openCategory(id, item.getTitle().toString());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCategoryClick(@NonNull Category category) {
        openCategory(category.id, category.toString());
    }

    private void openCategory(int categoryId, String title) {
        if (title != null) setTitle(title);
        switch (categoryId) {
            case 3:
                setFragment(StaffsFragment.newInstance());
                break;
            case 7:
                setFragment(GalleryFragment.newInstance());
                break;
            default:
                Toast.makeText(this, "id: " + categoryId, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_holder, fragment)
                .addToBackStack(null).commit();
    }

}
