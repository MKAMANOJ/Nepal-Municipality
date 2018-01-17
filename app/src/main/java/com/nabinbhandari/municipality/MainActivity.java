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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.nabinbhandari.LanguageHelper;
import com.nabinbhandari.municipality.contact.ContactFragment;
import com.nabinbhandari.municipality.content.ContentFragment;
import com.nabinbhandari.municipality.gallery.GalleryFragment;
import com.nabinbhandari.municipality.impcontacts.ImpContactsFragment;
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

    private DrawerLayout drawer;
    private MaterialMenuDrawable menu;
    private boolean isDrawerOpened;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.refreshLanguage(this);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        MenuFragment menuFragment = MenuFragment.newInstance(Category.getDummyList(), this);
        fragmentManager.beginTransaction().replace(R.id.fragment_holder, menuFragment).commit();

        initDrawer();
        initNavigationItems();

        PhotoService service = RetrofitUtils.getRetrofit().create(PhotoService.class);
        ImageUtils.init(this, service);
    }

    private void initDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int backStackCount = fragmentManager.getBackStackEntryCount();
                if (backStackCount == 0) {
                    drawer.openDrawer(Gravity.START);
                } else {
                    fragmentManager.popBackStack();
                }
            }
        });

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int backStackEntryCount = fragmentManager.getBackStackEntryCount();
                if (backStackEntryCount > 1) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                } else {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
                if (backStackEntryCount > 0) {
                    menu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
                } else {
                    ((NavigationView) (findViewById(R.id.nav_view))).setCheckedItem(R.id.nav_home);
                    menu.animateIconState(MaterialMenuDrawable.IconState.BURGER);
                    setTitle(R.string.app_name);
                }
                invalidateOptionsMenu();
            }
        });

        toolbar.setNavigationIcon(menu);

        drawer = findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                menu.setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        isDrawerOpened ? 2 - slideOffset : slideOffset
                );
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                isDrawerOpened = true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                isDrawerOpened = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_IDLE) {
                    if (isDrawerOpened) {
                        menu.setIconState(MaterialMenuDrawable.IconState.ARROW);
                    } else {
                        if (fragmentManager.getBackStackEntryCount() > 0) {
                            menu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
                        } else {
                            menu.setIconState(MaterialMenuDrawable.IconState.BURGER);
                        }
                    }
                }
            }
        });
    }

    private void initNavigationItems() {
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
        item.setVisible(fragmentManager.getBackStackEntryCount() == 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nepali) {
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
                fragmentManager.popBackStackImmediate();
            }
        } else openCategory(id, item.getTitle().toString());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCategoryClick(@NonNull Category category) {
        ((NavigationView) (findViewById(R.id.nav_view))).setCheckedItem(category.id);
        openCategory(category.id, category.toString());
    }

    private void openCategory(int categoryId, String title) {
        switch (categoryId) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                setFragment(ContentFragment.newInstance(categoryId));
                break;
            case 9:
                setFragment(CKEditorFragment.newInstance("tbl_introduction/1/content"));
                break;
            case 10:
                setFragment(StaffsFragment.newInstance());
                break;
            case 11:
                setFragment(GalleryFragment.newInstance());
                break;
            case 12:
                setFragment(ImpContactsFragment.newInstance());
                break;
            case 14:
                setFragment(ContactFragment.newInstance());
                break;
            default:
                Toast.makeText(this, "id: " + categoryId, Toast.LENGTH_SHORT).show();
                return;
        }
        if (title != null) setTitle(title);
    }

    private void setFragment(Fragment fragment) {
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
        fragmentManager.beginTransaction().replace(R.id.fragment_holder, fragment)
                .addToBackStack(null).commit();
    }

}
