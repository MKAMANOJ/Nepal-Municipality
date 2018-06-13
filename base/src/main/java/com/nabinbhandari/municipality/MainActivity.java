package com.nabinbhandari.municipality;

import android.content.Intent;
import android.content.res.Configuration;
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
import com.nabinbhandari.firebaseutils.RemoteConfig;
import com.nabinbhandari.municipality.contact.ContactFragment;
import com.nabinbhandari.municipality.content.ContentFragment;
import com.nabinbhandari.municipality.gallery.GalleryFragment;
import com.nabinbhandari.municipality.impcontacts.ImpContactsFragment;
import com.nabinbhandari.municipality.map.MapFragment;
import com.nabinbhandari.municipality.menu.Category;
import com.nabinbhandari.municipality.menu.MenuFragment;
import com.nabinbhandari.municipality.staffs.StaffsFragment;
import com.nabinbhandari.notification.NotificationsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuFragment.OnCategoryClickListener {

    public static boolean isBackground = true;

    private FragmentManager fragmentManager;
    private long backPressedTime;

    private DrawerLayout drawer;
    private MaterialMenuDrawable menu;
    private boolean isDrawerOpened;
    private boolean shouldShowBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isBackground = false;
        setContentView(R.layout.activity_main);
        findViewById(R.id.fragment_holder).setBackgroundColor(RemoteConfig.getMenuBackgroundColor());

        fragmentManager = getSupportFragmentManager();
        MenuFragment menuFragment = MenuFragment.newInstance(Category.getDummyList());
        fragmentManager.beginTransaction().replace(R.id.fragment_holder, menuFragment).commit();

        refreshLanguage();
        initDrawer();
        initNavigationItems();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        refreshLanguage();
        super.onConfigurationChanged(newConfig);
    }

    private void refreshLanguage() {
        LanguageHelper.refreshLanguage(this);
        if (fragmentManager.getBackStackEntryCount() == 0) setTitle(R.string.app_name_title);
    }

    private void initDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        menu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int backStackCount = fragmentManager.getBackStackEntryCount();
                if (shouldShowBackArrow || backStackCount > 1) {
                    fragmentManager.popBackStack();
                } else {
                    drawer.openDrawer(Gravity.START);
                }
            }
        });

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int backStackEntryCount = fragmentManager.getBackStackEntryCount();
                if (backStackEntryCount == 0) {
                    ((NavigationView) (findViewById(R.id.nav_view))).setCheckedItem(R.id.nav_home);
                    setTitle(R.string.app_name_title);
                    shouldShowBackArrow = false;
                }
                if (shouldShowBackArrow || backStackEntryCount > 1) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    menu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
                } else {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    menu.animateIconState(MaterialMenuDrawable.IconState.BURGER);
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
                        if (fragmentManager.getBackStackEntryCount() > 1) {
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
        boolean dynamicColor = getResources().getBoolean(R.bool.dynamic_color);
        int iconColor = dynamicColor ? RemoteConfig.getMenuIconColor() :
                ContextCompat.getColor(this, R.color.colorPrimary);
        if (drawable != null) drawable.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        menu.findItem(R.id.nav_home).setIcon(drawable);

        for (Category category : Category.getDummyList()) {
            menu.add(R.id.menuGroup, category.id, Menu.FIRST, category.toString());
            drawable = ContextCompat.getDrawable(this, category.getNavIconColor());
            if (drawable != null) drawable.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
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
        if (fragmentManager.getBackStackEntryCount() > 0) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.nepali);
        item.setChecked(LanguageHelper.isNepali(this));
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
        } else if (id == R.id.rateApp) {
            AppUtils.openPlayStore(this, getString(R.string.error_play_store_not_found));
        } else if (id == R.id.notifications) {
            setFragment(NotificationsFragment.newInstance());
            setTitle(R.string.notifications);
            shouldShowBackArrow = true;
        } else if (id == R.id.about_app) {
            setFragment(CKEditorFragment.newInstance("tbl_introduction/2/content"));
            setTitle(R.string.about_app);
            shouldShowBackArrow = true;
        } else if (id == R.id.licenses) {
            setFragment(LicenseFragment.newInstance());
            setTitle(R.string.license);
            shouldShowBackArrow = true;
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
            case 9:
                setFragment(ContentFragment.newInstance(categoryId));
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
            case 13:
                setFragment(CKEditorFragment.newInstance("tbl_introduction/1/content"));
                break;
            case 14:
                setFragment(ContactFragment.newInstance());
                break;
            case 15:
                setFragment(MapFragment.newInstance());
                break;
            default:
                Toast.makeText(this, "Invalid id: " + categoryId, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        isBackground = true;
        super.onDestroy();
    }

}
