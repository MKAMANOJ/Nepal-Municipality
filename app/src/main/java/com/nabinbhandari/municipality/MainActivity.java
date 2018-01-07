package com.nabinbhandari.municipality;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.LanguageHelper;
import com.nabinbhandari.municipality.gallery.GalleryFragment;
import com.nabinbhandari.municipality.menu.Category;
import com.nabinbhandari.municipality.menu.MenuFragment;
import com.nabinbhandari.retrofit.ImageUtils;
import com.nabinbhandari.retrofit.PhotoService;
import com.nabinbhandari.retrofit.RetrofitUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int NAV_ICON_COLOR = Color.BLUE;

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

        MenuFragment menuFragment = MenuFragment.newInstance(Category.getDummyList());
        setFragment(menuFragment, R.string.app_name);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        //noinspection SimplifiableIfStatement
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            MenuFragment menuFragment = MenuFragment.newInstance(Category.getDummyList());
            setFragment(menuFragment, R.string.app_name);
        } else if (id == 3) {
            testFirebaseDatabase();
        } else if (id == 7) {
            setFragment(GalleryFragment.newInstance(), R.string.app_name);
        } else {
            Toast.makeText(this, "id: " + id, Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void testFirebaseDatabase() {
        FirebaseDatabase.getInstance().getReference("tbl_staffs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        System.err.println("value: " + dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("error");
                    }
                });
    }

    private void setFragment(Fragment fragment, @StringRes int title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, fragment)
                .commit();
        if (title != 0 && getActionBar() != null) {
            getActionBar().setTitle(title);
        }
    }

}
