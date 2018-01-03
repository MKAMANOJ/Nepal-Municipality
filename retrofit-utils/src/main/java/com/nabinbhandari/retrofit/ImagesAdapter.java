package com.nabinbhandari.retrofit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created at 9:42 PM on 12/25/2017.
 *
 * @author bnabin51@gmail.com
 */

class ImagesAdapter extends FragmentStatePagerAdapter {

    private final List<Image> images;

    ImagesAdapter(FragmentManager fm, List<Image> images) {
        super(fm);
        this.images = images;
    }

    @Override
    public Fragment getItem(int position) {
        Image image = images.get(position);
        return ImageFragment.newInstance(image.url, image.description);
    }

    @Override
    public int getCount() {
        return images.size();
    }

}
