package com.nabinbhandari.municipality;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nabinbhandari.municipality.menu.Category;
import com.nabinbhandari.municipality.menu.MenuFragment;
import com.nabinbhandari.retrofit.ImageUtils;
import com.nabinbhandari.retrofit.PhotoService;
import com.nabinbhandari.retrofit.RetrofitUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        for (Category category : Category.getDummyList()) {
            menu.add(R.id.menuGroup, category.id, Menu.FIRST, category.toString());
            menu.findItem(category.id).setCheckable(true).setIcon(category.resId);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
        } else {
            Toast.makeText(this, "id: " + id, Toast.LENGTH_SHORT).show();
        }
        // setFragment(GalleryFragment.newInstance(), R.string.introduction);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(Fragment fragment, @StringRes int title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, fragment)
                .commit();
        if (title != 0 && getActionBar() != null) {
            getActionBar().setTitle(title);
        }
    }

}
